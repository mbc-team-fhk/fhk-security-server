package com.fhk.security.models.account.service;

import com.fhk.security.models.account.domain.Account;
import com.fhk.security.models.account.dto.postAccount.PostAccountReq;
import com.fhk.security.models.account.dto.postAccount.PostAccountRes;
import com.fhk.security.models.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public PostAccountRes postAccount(PostAccountReq request) {
        var loginId = request.getLoginId();
        var loginPw = request.getLoginPw();

        if (accountRepository.existsByLoginId(loginId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "exist id");
        }

        var newAccount = new Account();
        newAccount.setLoginId(loginId);
        newAccount.setPassword(passwordEncoder.encode(loginPw));
        var created = accountRepository.save(newAccount);
        return PostAccountRes.builder()
                .accountId(created.getId())
                .loginId(loginId)
                .build();
    }

    @Override
    public void getAccountDetail() {

    }
}
