package com.fhk.security.models.account.dto.joinAccount;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Getter
public class JoinAccountReq {
    private String loginId;
    private String loginPw;
}
