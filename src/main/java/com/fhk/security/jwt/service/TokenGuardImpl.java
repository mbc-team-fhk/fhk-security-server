package com.fhk.security.jwt.service;

import com.fhk.security.core.interfaces.TokenGuard;
import com.fhk.security.models.account.repository.AccountRepository;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

@Component
@AllArgsConstructor
public class TokenGuardImpl implements TokenGuard {

	private final StringRedisTemplate redisTemplate;
	private final AccountRepository accountRepository;

	/**
	 * 토큰 버전 검증
	 *
	 * @param uid accountId
	 * @param tokenVersion
	 */
	private void checkVersion(long uid, Integer tokenVersion) {
		String redisKey = "fhk:security:account:" + uid + ":ver"; // 캐시 DB 토큰 value KEY
		String redisValue = redisTemplate.opsForValue().get(redisKey);
		int currentVersion = (redisValue != null) ?
				Integer.parseInt(redisValue) : accountRepository.findTokenVersionById(uid).intValue();

		if (redisValue == null)
			redisTemplate.opsForValue().set(redisKey,
					Integer.toString(currentVersion),
					java.time.Duration.ofMinutes(15));

		if (tokenVersion == null || tokenVersion != currentVersion)
			throw new CredentialsExpiredException("ver mismatch");
	}

	@Override
	public void verifyAccess(Claims claims) {
		long uid = Long.parseLong(claims.getSubject());
		Integer tokenVersion = claims.get("version", Integer.class);

		// aud 검증
		if (!"access".equals(claims.getAudience())) {
			throw new BadCredentialsException("not access token");
		}

		// token 내 만료기간 없음
		Date expDate = claims.getExpiration();
		if (expDate == null)
			throw new BadCredentialsException("exp missing");

		checkVersion(uid, tokenVersion);
	}

	@Override
	public void verifyRefresh(Claims claims) {
		long uid = Long.parseLong(claims.getSubject());
		Integer tokenVersion = claims.get("version", Integer.class);

		// aud == refresh 검증
		if (!"refresh".equals(claims.getAudience())) {
			throw new BadCredentialsException("not refresh token");
		}

		// token 내 만료기간 없음
		Date expDate = claims.getExpiration();
		if (expDate == null)
			throw new BadCredentialsException("exp missing");

		checkVersion(uid, tokenVersion);
	}
}
