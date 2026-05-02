package com.fhk.security.auth.dto.performLogin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PerformLoginReq {
	@NotBlank(message = "login Id is required")
	@Size(min = 6, max = 20, message = "login Id must be between 6 and 20 characters")
	private String loginId;

	@NotBlank(message = "login Password is required")
	@Size(min = 8, max = 24, message = "login Password must be between 8 and 24 characters")
	private String loginPw;
}
