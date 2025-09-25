package com.fhk.security.config;

import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class SecurityWhiteList {

	/**
	 * [Options]
	 * :OPTIONS, :POST, :GET, :PUT, :PATCH, :DELETE
	 *
	 *   (ex)
		 "/api/auth/v1/**",
		 "/api/accounts:POST",   // 메서드까지 묶고 싶을 때 추가
		 "/v3/api-docs/",
		 "/swagger-ui.html",
		 "/swagger-ui/"
	 *
	 */
	public List<String> getUris() {
		return List.of(
				"/api/auth/v1/**",
				"/api/accounts:POST",
				"/v3/api-docs/",
				"/swagger-ui.html",
				"/swagger-ui/"
		);
	}
}
