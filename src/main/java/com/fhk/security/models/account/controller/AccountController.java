package com.fhk.security.models.account.controller;

import com.fhk.security.models.account.dto.joinAccount.JoinAccountReq;
import com.fhk.security.models.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    // Create
    @PostMapping
    public void postAccount(@RequestBody JoinAccountReq request) {
        accountService.postAccount(request);
    }

    // Read One
    @GetMapping
    public void getAccountDetail() {
        accountService.getAccountDetail();
    }
}
