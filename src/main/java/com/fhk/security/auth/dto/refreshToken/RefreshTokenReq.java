package com.fhk.security.auth.dto.refreshToken;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class RefreshTokenReq {
	@NotNull
	private String refreshToken;
}
