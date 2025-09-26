package pre.study.spring7._global.config.jwt;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import jakarta.servlet.http.HttpServletRequest;

/**
 * jwt관련 간단 utils
 * 
 * */
public final class JwtUtil {
	private JwtUtil() {}

    // Authorization Bearer 토큰 추출 */
    public static String resolveToken(HttpServletRequest req) {
        String auth = req.getHeader(HttpHeaders.AUTHORIZATION);
        return (auth != null && auth.startsWith("Bearer ")) ? auth.substring(7) : null;
    }

    // ROLE 문자열 리스트 -> Spring Security 권한 객체 리스트
    public static List<SimpleGrantedAuthority> toAuthorities(List<String> roles) {
        return (roles == null) ? List.of() : roles.stream().map(SimpleGrantedAuthority::new).toList();
    }
}
