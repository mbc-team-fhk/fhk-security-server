package com.fhk.security.models.account.service;

import com.fhk.security.models.account.domain.Account;
import com.fhk.security.models.account.dto.getAccount.GetAccountByLoginIdRes;
import com.fhk.security.models.account.dto.getAccountByNickname.GetAccountByNicknameRes;
import com.fhk.security.models.account.dto.isAvailability.AvailabilityRes;
import com.fhk.security.auth.dto.getMe.GetMeRes;
import com.fhk.security.models.account.dto.modifyAccount.ModifyAccountReq;
import com.fhk.security.models.account.dto.modifyAccount.ModifyAccountRes;
import com.fhk.security.models.account.dto.postAccount.PostAccountReq;
import com.fhk.security.models.account.dto.postAccount.PostAccountRes;
import com.fhk.security.models.account.dto.withdraw.WithdrawRes;
import com.fhk.security.models.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

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
	public GetMeRes getMe(Long accountId) {
		var account = accountRepository.findById(accountId).orElseThrow(NoSuchElementException::new);

		return GetMeRes.builder()
				.accountId(account.getId())
				.loginId(account.getLoginId())
				.nickname(account.getNickname())
				.role(account.getRole().toString())
				.build();
	}

	@Override
	public GetAccountByLoginIdRes getAccountByLoginId(String loginId) {
		var account = accountRepository.findByLoginId(loginId).orElseThrow(NoSuchElementException::new);

		return GetAccountByLoginIdRes.builder()
				.accountId(account.getId())
				.loginId(account.getLoginId())
				.nickname(account.getNickname())
				.role(account.getRole().toString())
				.build();
	}

	@Override
	public GetAccountByNicknameRes getAccountByNickname(String nickname) {
		var account = accountRepository.findByNickname(nickname).orElseThrow(NoSuchElementException::new);

		return GetAccountByNicknameRes.builder()
				.accountId(account.getId())
				.loginId(account.getLoginId())
				.nickname(account.getNickname())
				.role(account.getRole().toString())
				.build();
	}

	@Override
	public AvailabilityRes isAvailability(String loginId) {
		boolean available = !accountRepository.existsByLoginId(loginId);

        return AvailabilityRes.builder()
				.available(available)
				.build();
	}

	@Override
	public ModifyAccountRes modifyAccount(Long id, ModifyAccountReq body) {
		var account = accountRepository.findById(id).orElseThrow(NoSuchElementException::new);

		// 1. 비밀번호 검증
		if (!passwordEncoder.matches(body.getPassword(), account.getPassword()))
			throw new BadCredentialsException("invalid credentials");

		// 2. 정보 수정
		if (body.getNickname() != null)
			account.setNickname(body.getNickname());

		if (body.getPassword() != null && !body.getPassword().isBlank()) {
			account.setPassword(passwordEncoder.encode(body.getPassword()));
		}

		// TODO 3. 로그아웃 처리? -> 리프래시 토큰 폐기 처리?

		return ModifyAccountRes.builder()
				.id(account.getId())
				.nickname(account.getNickname())
				.build();
	}

	@Override
	public WithdrawRes withdrawAccount(Long id) {
		var account = accountRepository.findById(id).orElseThrow(NoSuchElementException::new);

		account.setDeletedYn("Y");

		return WithdrawRes.builder()
				.id(account.getId())
				.nickname(account.getNickname())
				.withdrawDate(account.getUpdateTime())
				.build();
	}
}
