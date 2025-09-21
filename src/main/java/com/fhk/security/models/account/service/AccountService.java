package com.fhk.security.models.account.service;

import com.fhk.security.models.account.dto.joinAccount.JoinAccountReq;

public interface AccountService {

    void postAccount(JoinAccountReq request);

    void getAccountDetail();
}
