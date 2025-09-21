package com.fhk.security.models.account.repository;

import com.fhk.security.models.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

}
