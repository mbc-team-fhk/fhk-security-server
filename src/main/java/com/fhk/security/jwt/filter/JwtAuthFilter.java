package com.fhk.security.jwt.filter;

import com.fhk.security.config.SecurityWhiteList;
import com.fhk.security.jwt.JwtProvider;
import com.fhk.security.jwt.TokenGuard;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

//@Component -> Security Config 에서 Bean 추가
@RequiredArgsConstructor
@Log4j2
public class JwtAuthFilter extends OncePerRequestFilter {

	private final JwtProvider jwt;
	private final TokenGuard tokenGuard; // redis 적용 필요
	private final SecurityWhiteList securityWhiteList;

	/**
	 * OncePerRequestFilter
	 * 에서 먼저 호출됨
	 *
	 * 토큰 검증 스킵 목록
	 */
	@Override
	protected boolean shouldNotFilter(HttpServletRequest httpRequest) {
		var uri = httpRequest.getRequestURI();
		var method = httpRequest.getMethod();
		log.info("skip? {} {} type={}", method, uri, httpRequest.getDispatcherType());
		log.info("skip? {} {} => {}", method, uri, "...skip");
		// 이번 요청에 대하여
		// 화이트리스트와 비교
		return securityWhiteList.getUris().stream().anyMatch(white -> {

			if (white.contains(":")) {
				String whiteUri = white.split(":")[0];
				String whiteMethod = white.split(":")[1];

				return method.equals(whiteMethod) && uri.equals(whiteUri);
			}

			return uri.startsWith(white);
		});
	}

	/**
	 * shouldNotFilter 2번 타지않게 처리
	 */
	@Override
	protected boolean shouldNotFilterErrorDispatch() {
		return super.shouldNotFilterErrorDispatch();
	}

	/**
	 * JWT 토큰 인증
	 * Access 토큰만 검증하며
	 * <p>
	 * /auth/refresh = 인증 대상이 아닌 api ( /auth/** )
	 * Service 에서 검증한다.
	 *
	 * @param req
	 * @param res
	 * @param chain
	 * @throws ServletException
	 * @throws IOException
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws ServletException, IOException {

		log.info("jwt auth filter in...");

		// CORS 등 preflight 요청은 JWT 인증 skip
		if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
			chain.doFilter(req, res);
			return;
		}

		// 다른 Filter 에서 상위 인증을 통해 SecurityContext 가 이미 주입된 경우 JWT 인증 skip
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			chain.doFilter(req, res);
			return;
		}

		// 헤더가 Authorization Bearer 가 아닌경우 JWT 인증 skip
		// 그냥 skip 시켜도 이후 authorizeHttpRequests 단계에서 EntryPoint 를 통해 미인증 보호 API 요청에 401 리턴
		String HEADER_STRING = "Authorization";
		String TOKEN_PREFIX = "Bearer ";

		var header = req.getHeader(HEADER_STRING);
		log.info(header);
		if (header == null || !header.startsWith(TOKEN_PREFIX)) {
			chain.doFilter(req, res);
			return;
		}

		// 토큰 인증 로직
		// Access 토큰 검증은  컨트롤러로 넘기지 말것.  redis 캐시 DB 등을 사용해 필터 내에서 검증 끝낼것.
		// 임시 처리로 일단 TokenGuard -> DB 넘김 +++++
		var token = header.substring(TOKEN_PREFIX.length());
		log.info(token);

		try {
			var claims = jwt.parseAccessToken(token).getBody();
			tokenGuard.verifyAccess(claims);

			var principal = jwt.getUserPrincipal(token);
			log.info(principal.toString());
			var authorities = AuthorityUtils.createAuthorityList("ROLE_" + principal.role());

			// Token 인증 기반은 credentials 필요 없음 -> null 처리
			var auth = new UsernamePasswordAuthenticationToken(principal, null, authorities);
			auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));

			// 해당 endpoint 요청에 대한 Security Context 유지 설정
			var context = SecurityContextHolder.createEmptyContext();
			context.setAuthentication(auth);
			SecurityContextHolder.setContext(context);

			chain.doFilter(req, res);

		} catch (ResponseStatusException e) {
			log.info("token error: {}", e.getReason());
			res.sendError(e.getStatusCode().value(), e.getReason());
		} catch (JwtException ex) { // io.jsonwebtoken 계열
			log.info(ex.getMessage());
			log.info("Invalid or expired token");
			res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
		}
	}
}
