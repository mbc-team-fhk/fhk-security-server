package com.fhk.security.models.account.dto.getAccountByNickname;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetAccountByNicknameRes {
    private Long accountId;
    private String loginId;
    private String nickname;
    private String role;
}
