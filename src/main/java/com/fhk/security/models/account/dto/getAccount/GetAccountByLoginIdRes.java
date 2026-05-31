package com.fhk.security.models.account.dto.getAccount;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetAccountByLoginIdRes {
    private Long accountId;
    private String loginId;
    private String nickname;
    private String role;
}
