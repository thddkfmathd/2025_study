package pre.study.spring7._global.config.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * application.yml 의 app.jwt.* 값 바인딩.
 * record 로 구성 (Spring Boot 3.x에서 record @ConfigurationProperties 지원
 * 
 * @EnableConfigurationProperties(JwtProperties.class) 로 등록되어야 함(SecurityConfig에 넣음)
 * */

@Validated
@ConfigurationProperties(prefix = "app.jwt") // yml수정해야하면 얘도수정
public record JwtProperties(
		@NotBlank String secret,
	    @NotBlank String issuer,
	    @NotNull Long accessTokenValidityMs) {}
