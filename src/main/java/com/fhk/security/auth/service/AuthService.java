package com.fhk.security.auth.service;

import com.fhk.common.api.servlet.ClientInfo;
import com.fhk.security.auth.dto.performLogin.PerformLoginReq;
import com.fhk.security.auth.dto.performLogin.PerformLoginRes;
import com.fhk.security.auth.dto.refreshToken.RefreshTokenReq;
import com.fhk.security.auth.dto.refreshToken.RefreshTokenRes;

public interface AuthService {

	PerformLoginRes performLogin(PerformLoginReq req, ClientInfo clientInfo);
	RefreshTokenRes refreshToken(RefreshTokenReq req, ClientInfo clientInfo);
}
