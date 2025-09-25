package com.fhk.security.models.account.service;

import com.fhk.security.models.account.dto.postAccount.PostAccountReq;
import com.fhk.security.models.account.dto.postAccount.PostAccountRes;

public interface AccountService {

    PostAccountRes postAccount(PostAccountReq request);

    void getAccountDetail();
}
