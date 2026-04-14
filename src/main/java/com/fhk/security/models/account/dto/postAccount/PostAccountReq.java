package com.fhk.security.models.account.dto.postAccount;

import lombok.Getter;

@Getter
public class PostAccountReq {
    private String loginId;
    private String loginPw;
    private String nickName;
}
