package com.fhk.security.models.account.service;

import com.fhk.security.models.account.dto.getAccount.GetAccountByLoginIdRes;
import com.fhk.security.models.account.dto.getAccountByNickname.GetAccountByNicknameRes;
import com.fhk.security.models.account.dto.isAvailability.AvailabilityRes;
import com.fhk.security.auth.dto.getMe.GetMeRes;
import com.fhk.security.models.account.dto.modifyAccount.ModifyAccountReq;
import com.fhk.security.models.account.dto.modifyAccount.ModifyAccountRes;
import com.fhk.security.models.account.dto.postAccount.PostAccountReq;
import com.fhk.security.models.account.dto.postAccount.PostAccountRes;
import com.fhk.security.models.account.dto.withdraw.WithdrawRes;
import org.springframework.transaction.annotation.Transactional;

public interface AccountService {

    PostAccountRes postAccount(PostAccountReq request);
    GetMeRes getMe(Long id);
    ModifyAccountRes modifyAccount(Long id, ModifyAccountReq body);
    WithdrawRes withdrawAccount(Long id);

    GetAccountByLoginIdRes getAccountByLoginId(String loginId);
    GetAccountByNicknameRes getAccountByNickname(String nickname);

    AvailabilityRes isAvailabilityByLoginId(String loginId);
    AvailabilityRes isAvailabilityByNickname(String nickname);

}
