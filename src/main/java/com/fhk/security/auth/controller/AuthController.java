package com.fhk.security.auth.controller;

import com.fhk.common.api.ApiResponse;
import com.fhk.common.api.servlet.ClientInfo;
import com.fhk.security.auth.dto.performLogin.PerformLoginReq;
import com.fhk.security.auth.dto.refreshToken.RefreshTokenReq;
import com.fhk.security.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/auth/v1"})
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;

	@PostMapping("/login")
	public ResponseEntity<?> performLogin(@RequestBody PerformLoginReq request,
	                                      HttpServletRequest httpRequest) {

		var clientInfo = new ClientInfo(httpRequest);
		var res = authService.performLogin(request, clientInfo);

		return ApiResponse.ok(res);
	}

	@PostMapping("/refresh")
	public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenReq request,
	                                      HttpServletRequest httpRequest) {

		// RTR (Refresh Token Rotation)
		var clientInfo = new ClientInfo(httpRequest);
		var res = authService.refreshToken(request, clientInfo);

		return ApiResponse.ok(res);
	}
}
