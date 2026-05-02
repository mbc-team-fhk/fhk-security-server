package com.fhk.security.auth.service;

import com.fhk.common.api.servlet.ClientInfo;
//import com.fhk.core.config.jwt.RefreshTokenHasher;
import com.fhk.security.auth.dto.performLogin.PerformLoginReq;
import com.fhk.security.auth.dto.performLogin.PerformLoginRes;
import com.fhk.security.auth.dto.refreshToken.RefreshTokenReq;
import com.fhk.security.auth.dto.refreshToken.RefreshTokenRes;
import com.fhk.security.models.userRefreshToken.repository.UserRefreshTokenRepository;
import com.fhk.security.core.enums.Role;
import com.fhk.security.core.jwt.service.TokenGuard;
import com.fhk.security.core.jwt.JwtIssuer;
import com.fhk.security.core.jwt.JwtVerifier;
import com.fhk.security.core.jwt.utils.RefreshTokenHasher;
import com.fhk.security.models.account.repository.AccountRepository;
import com.fhk.security.models.userRefreshToken.domain.UserRefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

	private final StringRedisTemplate redisTemplate;

	private final JwtIssuer jwtIssuer;
	private final PasswordEncoder passwordEncoder;
	private final RefreshTokenHasher refreshTokenHasher;

	private final AccountRepository accountRepository;
	private final UserRefreshTokenRepository userRefreshTokensRepository;
	private final TokenGuard tokenGuard;
	private final JwtVerifier jwtVerifier;

	@Override
	@Transactional
	public PerformLoginRes performLogin(PerformLoginReq req, ClientInfo clientInfo) {

		var account = accountRepository.findByLoginId(req.getLoginId())
				.orElseThrow(() -> new BadCredentialsException("invalid credentials"));

		// 1. 비밀번호 검증
		if (!passwordEncoder.matches(req.getLoginPw(), account.getPassword()))
			throw new BadCredentialsException("invalid credentials");

		// TODO : 회원탈퇴 상태인 회원 로그인 반려처리

		// 2. token 버전 ++ (잠금)
		var ver = account.getTokenVersion();
		int updated = accountRepository.bumpVersionIfMatch(account.getId(), ver);
		if (updated != 1)
			throw new CredentialsExpiredException("stale token"); // 동시 갱신 차단

		// 3. 신규 토큰 생성
		var newVer = ver + 1;
		var accessToken = jwtIssuer.issueAccessToken(account.getId(), account.getRole().toString(), newVer);
		var refreshToken = jwtIssuer.issueRefreshToken(account.getId(), newVer);

		var claims = jwtVerifier.getClaims(refreshToken);
		String jti = claims.getId();
		String tokenHash = refreshTokenHasher.hash(refreshToken);
		LocalDateTime expiresAt = claims.getExpiration()
				.toInstant()
				.atZone(ZoneId.of("UTC"))
				.toLocalDateTime();

		UserRefreshToken newToken = UserRefreshToken.createToken(
				account, jti, tokenHash, expiresAt,
				clientInfo.getDeviceId(), clientInfo.getUserAgent(), clientInfo.getIp()
		);

		// refresh token 저장
		userRefreshTokensRepository.save(newToken);

		String redisKey = "fhk:security:account:" + account.getId() + ":ver"; // 캐시 DB 토큰 value KEY
		redisTemplate.opsForValue().set(redisKey,
				Long.toString(newVer),
				java.time.Duration.ofMinutes(15));

		return PerformLoginRes.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();
	}

	@Override
	@Transactional
	public RefreshTokenRes refreshToken(RefreshTokenReq req, ClientInfo clientInfo) {
		String refreshToken = req.getRefreshToken();
		var claims = jwtVerifier.getClaims(refreshToken);

		// refresh token 검증 ( aud, exp )
		tokenGuard.verifyRefresh(claims);


		// 1. token 분해
		var accountId = Long.parseLong(claims.getSubject());
		var ver = claims.get("version", Long.class);
		var jti = claims.getId(); // UUID


		// 2. 무결성 검증? + DB Lock
		var oldRefreshToken = userRefreshTokensRepository.findByJtiForUpdate(jti)
				.orElseThrow(() -> new BadCredentialsException("invalid credentials"));

		// --- 멤버 검증
		if (!oldRefreshToken.getAccount().getId().equals(accountId))
			throw new BadCredentialsException("unauthorized");

		// --- 교체된 jti 검증
		if (oldRefreshToken.getReplacedDate() != null)
			throw new BadCredentialsException("revoked");

		// --- 토큰 Hash 검증
		if (!refreshTokenHasher.matches(refreshToken, oldRefreshToken.getTokenHash()))
			throw new BadCredentialsException("mismatch");


		// 3. token 버전 ++ (잠금)
		int updated = accountRepository.bumpVersionIfMatch(accountId, ver);
		if (updated != 1)
			throw new CredentialsExpiredException("stale token"); // 동시 갱신 차단


		// 4. 신규 토큰 생성
		// 버전은 변수에서 처리
		Long newVer = ver + 1;

		// 권한은 권한만 조회,  rotate 를 위한 member 참조는 reference 로 호출한다. (select 쿼리 호출되지 않음)
		Role role = accountRepository.findRoleByAccountId(accountId);
		if (role == null)
			throw new BadCredentialsException("unauthorized");

		var memberRef = accountRepository.getReferenceById(accountId);

		var newAccessToken = jwtIssuer.issueAccessToken(accountId, role.toString(), newVer);
		var newRefreshToken = jwtIssuer.issueRefreshToken(accountId, newVer);

		var newClaims = jwtVerifier.getClaims(newRefreshToken);
		String newJti = newClaims.getId();
		String tokenHash = refreshTokenHasher.hash(newRefreshToken);
		LocalDateTime expiresAt = newClaims.getExpiration()
				.toInstant()
				.atZone(ZoneId.of("UTC"))
				.toLocalDateTime();

		UserRefreshToken newToken = UserRefreshToken.createToken(
				memberRef, newJti, tokenHash, expiresAt,
				clientInfo.getDeviceId(), clientInfo.getUserAgent(), clientInfo.getIp()
		);


		// 신규 refresh token 저장
		userRefreshTokensRepository.save(newToken);

		String redisKey = "fhk:security:account:" + accountId + ":ver"; // 캐시 DB 토큰 value KEY
		redisTemplate.opsForValue().set(redisKey,
				Long.toString(newVer),
				java.time.Duration.ofMinutes(15));


		// 5. 기존 refresh token rotate
		oldRefreshToken.rotate(newClaims.getId());

		return RefreshTokenRes.builder()
				.accessToken(newAccessToken)
				.refreshToken(newRefreshToken)
				.build();
	}
}
