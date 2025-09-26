package pre.study.spring7._global.config.jwt;


import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

//매 요청마다 Authorization: Bearer <token>을 읽어 토큰 검증 → SecurityContext에 인증 주입
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pre.study.spring7._global.security.RequestContext;
import pre.study.spring7._global.security.UserContext;

/**
 * API 체인에서만 JWT 인증 필터
*  1) Authorization: Bearer {token} 추출
 *  2) 유효하면 userId/roles 파싱 → SecurityContext 설정
 *  3) RequestContext(ThreadLocal)에도 UserContext 저장(간단 사용)
 *  4) finally에서 RequestContext/SecurityContext/MDC 정리
 * */

@Component
public class AuthTokenFilter extends OncePerRequestFilter{
	private final JwtTokenProvider tokenProvider;
	private final UserDetailsService userDetailsService;
	public AuthTokenFilter(JwtTokenProvider tokenProvider,UserDetailsService userDetailsService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }
	
	@Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        // 로깅 
		// Mapped Dialnostic Context 
		// key:value 현재 스레드 전용 로그 컨텍스트임
        MDC.put("requestId", UUID.randomUUID().toString());

        try {
        	String uri = req.getRequestURI();    
        	boolean isApi = uri.startsWith("/api/");
        	
            String token = JwtUtil.resolveToken(req);
            
            if (token != null && tokenProvider.validate(token)) {
                String userId = tokenProvider.getUserId(token);
                
                //1. 사용자정보조회 userContext설정
                UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
                // 2) SecurityContext 설정: UserDetails 사용
                var authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String role = userDetails.getAuthorities().stream()
                        .findFirst().map(a -> a.getAuthority()).orElse(null);

                // 2. userContext 세팅
                RequestContext.set(new UserContext(
                        userId,
                        userDetails.getUsername(),
                        role
                ));
                
                // header에 넣는 커스텀헤더(멀티테넌시) ex : X-Project-Id: proj-123 
                // 해당없음
                //String projectId = req.getHeader("X-Project-Id");

                // 3) MDC(로그)
                MDC.put("userId", userId);            
            }

            chain.doFilter(req, res);
        } finally {
            // ******** 다음 요청에 값이 섞이면 안됨
            RequestContext.clear();
            SecurityContextHolder.clearContext();
            MDC.clear();
        }
    }
}
