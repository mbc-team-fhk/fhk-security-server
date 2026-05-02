package com.fhk.security.auth.dto.performLoginWithKakao;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PerformLoginWithKakaoReq {
	@NotBlank
	private String kakaoId;
}
