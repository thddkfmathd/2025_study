package pre.study.spring7._global.config.jwt;

import java.security.Key;
import java.util.Date;
import java.util.List;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

//토큰 발급/검증 담당(서명, 만료시간, 클레임 파싱).
/**
 * JJWT 기반 토큰 유틸.
 * - 대칭키(HS256) 사용. secret 길이는 256bit 이상 권장.
 * - createToken: userId/roles/issuer/만료 포함
 * - validate/getUserId/getRoles 제공
 */

public class JwtTokenProvider {
	private final Key key;
    private final String issuer;
    private final long validityMs;

    public JwtTokenProvider(JwtProperties props) {
        this.key = Keys.hmacShaKeyFor(props.secret().getBytes());
        this.issuer = props.issuer();
        this.validityMs = props.accessTokenValidityMs();
    }

    // accessToken 생성
    public String createToken(String userId, List<String> roles) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + validityMs);
        return Jwts.builder()
                .setSubject(userId)
                .claim("roles", roles) // 커스텀 클레임
                .setIssuer(issuer)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256) // 서명
                .compact();
    }

    // 토큰이 형식/서명/만료 등 유효 체크 */
    public boolean validate(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false; // 만료, 변조 등
        }
    }
    
    //userId추출
    public String getUserId(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    // roles 추출 — JJWT가 Object를 반환하므로 캐스팅 필요 
    @SuppressWarnings("unchecked") // ??? 타입을 알고있는데 왜 어노테이션다는지
    public List<String> getRoles(String token) {
        Object v = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().get("roles");
        return (List<String>) v;
    }
}
