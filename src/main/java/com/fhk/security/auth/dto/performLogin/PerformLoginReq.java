package com.fhk.security.auth.dto.performLogin;

import lombok.Data;

@Data
public class PerformLoginReq {
	private String loginId;
	private String loginPw;
}
