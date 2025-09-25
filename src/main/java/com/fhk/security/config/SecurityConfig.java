package com.fhk.security.config;

import com.fhk.security.jwt.JwtProvider;
import com.fhk.security.jwt.TokenGuard;
import com.fhk.security.jwt.filter.JwtAuthFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	/**
	 * [인증, 인가]
	 * JWT + Bearer Token ( JWT는 세션을 쓰지않고 클라이언트에서 토큰을 들고 api 호출시마다 토큰을 보낸다. 모바일앱에서 사용하는 api 혹은 MSA 개발시 사용된다 )
	 *
	 * 어플리케이션 시작시
	 * SecurityFilterChain 객체를 Bean 으로 등록
	 *
	 */
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http, SecurityWhiteList securityWhiteList,
	                                JwtProvider jwt, TokenGuard guard, SecurityWhiteList whiteList) throws Exception {

		http.csrf(AbstractHttpConfigurer::disable)
				.sessionManagement(sm -> {
					sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
				})
				/*.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
						.requestMatchers("/api/auth/v1/**").permitAll()
						.requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/accounts").permitAll()
						.anyRequest().authenticated()
				)*/
				.authorizeHttpRequests(auth -> {
						securityWhiteList.getUris().forEach(white -> {
							if (white.contains(":")) {
								String whiteUri = white.split(":")[0];
								String whiteMethod = white.split(":")[1];

								switch (whiteMethod) {
									case "OPTIONS" -> auth.requestMatchers(HttpMethod.OPTIONS, whiteUri).permitAll();
									case "POST" -> auth.requestMatchers(HttpMethod.POST, whiteUri).permitAll();
									case "GET" -> auth.requestMatchers(HttpMethod.GET, whiteUri).permitAll();
									case "PUT" -> auth.requestMatchers(HttpMethod.PUT, whiteUri).permitAll();
									case "PATCH" -> auth.requestMatchers(HttpMethod.PATCH, whiteUri).permitAll();
									case "DELETE" -> auth.requestMatchers(HttpMethod.DELETE, whiteUri).permitAll();
								}

							} else {
								auth.requestMatchers(white).permitAll();
							}
						});

						// 화이트 리스트 외에 모든 request 에 인증 적용
						auth.anyRequest().authenticated();
					}
				)
				.formLogin(AbstractHttpConfigurer::disable)
				.httpBasic(AbstractHttpConfigurer::disable)
				.exceptionHandling(e -> {
					// AuthenticationException만 401로
					e.authenticationEntryPoint((req, res, ex) -> {
						res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
					});

					// AccessDeniedException만 403으로
					e.accessDeniedHandler((req, res, ex) -> {
						res.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
					});
				})
				.addFilterBefore(new JwtAuthFilter(jwt, guard, whiteList), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
