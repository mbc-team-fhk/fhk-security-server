package com.fhk.security.models.account.dto.postAccount;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostAccountRes {
	private Long accountId;
	private String loginId;
}
