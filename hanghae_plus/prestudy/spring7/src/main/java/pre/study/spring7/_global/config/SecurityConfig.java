package pre.study.spring7._global.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;
import pre.study.spring7._global.config.jwt.AuthEntryPointJwt;
import pre.study.spring7._global.config.jwt.AuthTokenFilter;
import pre.study.spring7._global.config.jwt.JwtTokenProvider;


//보안 전역 규칙: 세션 STATELESS, 허용 URL(/api/auth/**)과 보호 URL 설정, **JWT 필터 등록.
/* 
	JWT + Stateless무상태
	웹 페이지가 있으면 폼로그인 + 세션 (CSRF ON)
	컨텍스트 접근 : SecurityContextHolder?
**** API 체인: JWT + STATELESS + CSRF OFF + AuthEntryPoint + AuthTokenFilter
**** Web 체인: 세션/폼로그인 + CSRF ON (JWT 필터 미적용)

* 1) 세션x 완전 stateless API에 JWT 인증사용
* 2) 공개 URL과 인증 URL 구분
* 3) 필터 체인에 우리가 만든 JWT 필터(AuthTokenFilter)를 UsernamePasswordAuthenticationFilter 앞에 삽입 왜?
* 4) AuthenticationEntryPoint : 401 (인증실패) 결과 JSON 리턴 
* 5) CORS, CSRF


*/


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final AuthTokenFilter authTokenFilter; // JWT를 파싱/검증하여 SecurityContext에 인증객체 주입
	private final AuthEntryPointJwt unauthorizedHandler; // 401 대응 JSON 엔트리포인트
	private final UserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
	// 비밀번호해싱용
	@Bean
	public PasswordEncoder passwordEncoder() {
	return new BCryptPasswordEncoder();
	}
	
	// security -> UserId/Password 인증 필요시 주입,사용
	// ++ UserDetailsService + DaoAuthenticationProvider 구성
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
	return configuration.getAuthenticationManager();
	}
	
	// CORS
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
	CorsConfiguration config = new CorsConfiguration();
	config.setAllowedOrigins(List.of("http://localhost:3000", "http://127.0.0.1:3000")); // 필요 도메인 등록
	config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
	config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Project-Id"));
	config.setAllowCredentials(false); // JWT는 쿠키가 아니라 헤더 사용 → 보통 false


	UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	source.registerCorsConfiguration("/**", config);
	return source;
	}
	
	/* !!!! 필터체인정의 !!!! */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	http
	// 1) CSRF 비활성화: REST API + JWT 환경에서는 비활성화가 일반적
	.csrf(csrf -> csrf.disable())
	// 2) CORS 활성화
	.cors(cors -> {})
	// 3) 예외 처리: 인증 실패(401)
	.exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler))
	// 4) 세션 정책: STATELESS (서버 세션 저장 X)
	.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	// 5) URL 인가 규칙
	.authorizeHttpRequests(auth -> auth
	
	// Swagger/OpenAPI 허용 (필요 시)
	//.requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
	
		// 인증 API 공개
		.requestMatchers("/api/auth/**").permitAll()
		// 헬스체크 등 공개 필요 엔드포인트
		.requestMatchers(HttpMethod.GET, "/actuator/health").permitAll()
		// 그 외 모두 인증 필요
		.anyRequest().authenticated()
	)

	// 6) 폼로그인/기본로그인 사용 안 함  ///???? HOW?
	.formLogin(login -> login.disable())
	.httpBasic(basic -> basic.disable());

	// 7) JWT 필터 삽입: UsernamePasswordAuthenticationFilter 전에 실행되어야 함
	http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

	return http.build();
	}
}

//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity
//@RequiredArgsConstructor
//public class SecurityConfig {
//
////	private final JwtAuthenticationFilter jwtFilter;
//	private final UserDetailsServiceImpl userDetailsService;
//    private final AuthEntryPointJwt unauthorizedHandler;
//	
//}
