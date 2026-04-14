package com.fhk.security.models.account.controller;

import com.fhk.common.api.ApiResponse;
import com.fhk.security.core.record.FhkUserPrincipal;
import com.fhk.security.models.account.dto.modifyAccount.ModifyAccountReq;
import com.fhk.security.models.account.dto.postAccount.PostAccountReq;
import com.fhk.security.models.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Log4j2
public class AccountController {

	private final AccountService accountService;

	/**
	 * 회원가입
	 * /accounts
	 *
	 * @param body
	 * @return
	 */
	@PostMapping
	public ResponseEntity<?> postAccount(@RequestBody PostAccountReq body) {

		return ApiResponse.created(accountService.postAccount(body));
	}


	/**
	 * 로그인 ID 존재여부 확인
	 * /accounts/availability?loginId=
	 *
	 * @param loginId
	 * @return
	 * available
	 */
	@GetMapping(path = "/availability", params = "loginId")
	public ResponseEntity<?> isAvailability(@RequestParam String loginId) {

		return ApiResponse.ok(accountService.isAvailability(loginId));
	}


	/**
	 * 로그인 ID로 계정정보 조회
	 * /accounts?loginId={loginId}
	 *
	 * @param loginId
	 * @return
	 * id, loginId, nickname, role
	 */
	@GetMapping(params = "loginId")
	public ResponseEntity<?> getAccountByLoginId(@RequestParam String loginId) {

		return ApiResponse.ok(accountService.getAccountByLoginId(loginId));
	}

	/**
	 * 닉네임으로 계정정보 조회
	 * /accounts?nickname={nickname}
	 *
	 * @param nickname
	 * @return
	 * id, loginId, nickname, role
	 */
	@GetMapping(params = "nickname")
	public ResponseEntity<?> getAccountByNickname(@RequestParam String nickname) {

		return ApiResponse.ok(accountService.getAccountByNickname(nickname));
	}

	/**
	 * 본인 정보 수정하기
	 * /accounts/modify
	 * body
	 * {
	 *     "password":"",
	 *     "nickname":""
	 * }
	 */
	@PatchMapping("/modify")
	public ResponseEntity<?> modifyAccount(@RequestBody ModifyAccountReq body,
										   @AuthenticationPrincipal FhkUserPrincipal principal) {

		return ApiResponse.ok(accountService.modifyAccount(principal.id(), body));
	}

	/**
	 * 계정 탈퇴하기
	 * /accounts/withdraw
	 */
	@PatchMapping("/withdraw")
	public ResponseEntity<?> withdrawAccount(@AuthenticationPrincipal FhkUserPrincipal principal) {

		return ApiResponse.ok(accountService.withdrawAccount(principal.id()));
	}
}
