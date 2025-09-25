package com.fhk.security.models.account.controller;

import com.fhk.common.api.ApiResponse;
import com.fhk.common.security.FhkUserPrincipal;
import com.fhk.security.models.account.dto.postAccount.PostAccountReq;
import com.fhk.security.models.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private static final Logger log = LogManager.getLogger(AccountController.class);
    private final AccountService accountService;

    // Create
    @PostMapping
    public ResponseEntity<?> postAccount(@RequestBody PostAccountReq request) {

        return ApiResponse.created(accountService.postAccount(request));
    }

    // Read One
    @GetMapping("/{accountId}")
    public ResponseEntity<?> getAccountDetail(
                                              @AuthenticationPrincipal FhkUserPrincipal principal) {
        log.info(principal.toString());

        accountService.getAccountDetail();
        return ApiResponse.ok(principal);
    }
}
