package com.fhk.security.auth.dto.performLoginWithGoogle;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PerformLoginWithGoogleReq {
	@NotBlank
	private String googleId;
}
