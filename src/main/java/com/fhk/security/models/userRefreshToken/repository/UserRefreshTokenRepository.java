package com.fhk.security.models.userRefreshToken.repository;

import com.fhk.security.models.userRefreshToken.domain.UserRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, Long> {

	@Query(value = "select * from user_refresh_tokens where jti = :jti for update", nativeQuery = true)
	Optional<UserRefreshToken> findByJtiForUpdate(@Param("jti") String jti);
}
