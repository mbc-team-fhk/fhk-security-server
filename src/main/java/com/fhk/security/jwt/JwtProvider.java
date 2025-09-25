package com.fhk.security.jwt;

import com.fhk.common.api.servlet.ClientInfo;
import com.fhk.common.enums.Role;
import com.fhk.common.security.FhkUserPrincipal;
import com.fhk.core.config.jwt.JwtProperties;
import com.fhk.core.config.jwt.RefreshTokenHasher;
import com.fhk.security.models.account.domain.Account;
import com.fhk.security.models.userRefreshToken.domain.UserRefreshToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtProvider {

	private static final Logger log = LogManager.getLogger(JwtProvider.class);
	private final SecretKey accessKey;
	private final long accessExpireMs;

	private final SecretKey refreshKey;
	private final long refreshExpireMs;

	public JwtProvider(JwtProperties props) {
		this.accessKey = Keys.hmacShaKeyFor(props.accessSecret().getBytes(StandardCharsets.UTF_8));
		this.accessExpireMs = props.accessExpireMs();
		this.refreshKey = Keys.hmacShaKeyFor(props.refreshSecret().getBytes(StandardCharsets.UTF_8));
		this.refreshExpireMs = props.refreshExpireMs();
		log.info(accessKey);
		log.info(accessExpireMs);
		log.info(refreshKey);
		log.info(refreshExpireMs);
	}

	/**
	 * 토큰 발급시 Entity
	 *
	 * @param account
	 * @param refreshToken
	 * @param clientInfo
	 * @return
	 */
	public UserRefreshToken toRefreshTokenEntity(Account account, String refreshToken, ClientInfo clientInfo, RefreshTokenHasher tokenHasher) {

		var tokenClaims = parseRefreshToken(refreshToken).getBody();
		var expLdt = Instant.ofEpochMilli(tokenClaims.getExpiration().getTime())
				.atZone(ZoneId.of("UTC"))
				.toLocalDateTime();

		var entity = new UserRefreshToken();
		entity.setAccount(account);
		entity.setJti(tokenClaims.getId());
		entity.setTokenHash(tokenHasher.hash(refreshToken));
		entity.setCreatedDate(LocalDateTime.now());
		entity.setExpiresDate(expLdt);
		entity.setDeviceId(clientInfo.getDeviceId());
		entity.setUserAgent(clientInfo.getUserAgent());
		entity.setIp(clientInfo.getIp());

		return entity;
	}

	public String issueAccessToken(Long userId, Role role, Long ver) {
		long now = System.currentTimeMillis();
		return Jwts.builder()
				.setSubject(String.valueOf(userId))
				.claim("role", role)
				.claim("version", ver)

				.setId(UUID.randomUUID().toString())
				.setAudience("access")
				.setIssuedAt(new Date(now))
				.setExpiration(new Date(now + accessExpireMs))

				.signWith(accessKey, SignatureAlgorithm.HS256)
				.compact();
	}

	public String issueRefreshToken(Long userId, Long ver) {
		long now = System.currentTimeMillis();
		return Jwts.builder()
				.setSubject(String.valueOf(userId))
				.claim("version", ver)

				.setId(UUID.randomUUID().toString())
				.setAudience("refresh")
				.setIssuedAt(new Date(now))
				.setExpiration(new Date(now + refreshExpireMs))

				.signWith(refreshKey, SignatureAlgorithm.HS256)
				.compact();
	}

	public Jws<Claims> parseAccessToken(String token) {
		return Jwts.parserBuilder().setSigningKey(accessKey).build().parseClaimsJws(token);
	}

	public Jws<Claims> parseRefreshToken(String token) {
		return Jwts.parserBuilder().setSigningKey(refreshKey).build().parseClaimsJws(token);
	}

	@Deprecated
	public Long getUserId(String token) {
		return Long.valueOf(parseAccessToken(token).getBody().getSubject());
	}

	@Deprecated
	public String getRole(String token) {
		return parseAccessToken(token).getBody().get("role", String.class);
	}

	public FhkUserPrincipal getUserPrincipal(String token) {
		var claims = parseAccessToken(token).getBody();

		var id = Long.valueOf(claims.getSubject());
		var role = claims.get("role", String.class);
		return new FhkUserPrincipal(id, role);
	}
}
