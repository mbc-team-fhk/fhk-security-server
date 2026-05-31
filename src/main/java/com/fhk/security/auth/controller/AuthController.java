package com.fhk.security.auth.controller;

import com.fhk.common.api.ApiResponse;
import com.fhk.common.api.servlet.ClientInfo;
import com.fhk.security.auth.dto.performLogin.PerformLoginReq;
import com.fhk.security.auth.dto.refreshToken.RefreshTokenReq;
import com.fhk.security.auth.service.AuthService;
import com.fhk.security.core.record.FhkUserPrincipal;
import com.fhk.security.models.account.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/auth"})
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;
	private final AccountService accountService;

	/**
	 * 로그인
	 * @param request
	 * @param httpRequest
	 * @return
	 */
	@PostMapping("/login")
	public ResponseEntity<?> performLogin(@Valid @RequestBody PerformLoginReq request,
	                                      HttpServletRequest httpRequest) {

		var clientInfo = new ClientInfo(httpRequest);
		var res = authService.performLogin(request, clientInfo);

		return ApiResponse.ok(res);
	}

	/**
	 * 토큰 재발급
	 * @param request
	 * @param httpRequest
	 * @return
	 */
	@PostMapping("/refresh")
	public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenReq request,
	                                      HttpServletRequest httpRequest) {

		// RTR (Refresh Token Rotation)
		var clientInfo = new ClientInfo(httpRequest);
		var res = authService.refreshToken(request, clientInfo);

		return ApiResponse.ok(res);
	}

	/**
	 * 토큰으로 본인 계정정보 조회
	 * /auth/me
	 *
	 * @param principal
	 * @return
	 * id, loginId, nickname, role
	 */
	@GetMapping("/me")
	public ResponseEntity<?> getMe(@AuthenticationPrincipal FhkUserPrincipal principal) {

		return ApiResponse.ok(accountService.getMe(principal.id()));
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(@AuthenticationPrincipal FhkUserPrincipal principal) {
		
		// TODO 로그아웃 후속조치
		// refresh 토큰 폐기
		// redis db 정리
		return ApiResponse.ok(null);
	}



	/**
	 * Kakao 로그인
	 * @param request
	 * @param httpRequest
	 * @return
	 */
	@PostMapping("/oauth/kakao/login")
	public ResponseEntity<?> performLoginWithKakao(@Valid @RequestBody PerformLoginReq request,
										  HttpServletRequest httpRequest) {

		return ApiResponse.ok(null);
	}

	/**
	 * Google 로그인
	 * @param request
	 * @param httpRequest
	 * @return
	 */
	@PostMapping("/oauth/google/login")
	public ResponseEntity<?> performLoginWithGoogle(@Valid @RequestBody PerformLoginReq request,
												   HttpServletRequest httpRequest) {

		return ApiResponse.ok(null);
	}
}
