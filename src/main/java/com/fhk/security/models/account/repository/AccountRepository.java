package com.fhk.security.models.account.repository;

import com.fhk.security.core.enums.Role;
import com.fhk.security.models.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

	Optional<Account> findByLoginId(String loginId);

	Optional<Account> findByNickname(String nickname);



	/**
	 * 토큰 버전 조회
	 *
	 * @param id
	 * @return
	 */
	@Query("select a.tokenVersion from Account a where a.id = :id")
	Long findTokenVersionById(@Param("id") Long id);

	/**
	 * 토큰 버전 원자적 증가
	 *
	 * @param id
	 * @param cur
	 * @return
	 */
	@Modifying
	@Query("update Account a set a.tokenVersion = a.tokenVersion + 1 where a.id = :id and a.tokenVersion = :cur")
	int bumpVersionIfMatch(@Param("id") Long id, @Param("cur") Long cur);

	/**
	 * 멤버 권한 조회
	 *
	 * @param id
	 * @return
	 */
	@Query("select a.role from Account a where a.id = :id")
	Role findRoleByAccountId(@Param("id") Long id);

	boolean existsByLoginId(String loginId);
}
