package pre.study.spring7._global.config.jwt;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 인증 실패(401) 시 체인에서 JSON으로 응답 , 엔트리포인트. 
 *  -> 인증없이 인증 URL 접근
 *  -> JWT 없, 만료,위조
 *  
 * Web 체인(세션/폼)은 로그인 페이지로 리다이렉트
 * API는 JSON이 적절.
 */

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint{
	private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);
	private final ObjectMapper om = new ObjectMapper();

	@Override
    public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException ex) throws IOException {
		logger.error("Unauthorized error: {}", ex.getMessage());
		res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        var body = Map.of(
                "success", false,
                "code", "UNAUTHORIZED",
                "message", "Unauthorized: " + ex.getMessage(),
                "timestamp", Instant.now().toString(),
                "path",req.getServletPath()
        );
        om.writeValue(res.getOutputStream(), body);
    }
}
