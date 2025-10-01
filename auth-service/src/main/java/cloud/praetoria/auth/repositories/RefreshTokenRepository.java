package cloud.praetoria.auth.repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import cloud.praetoria.auth.entities.RefreshToken;
import cloud.praetoria.auth.entities.User;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	Optional<RefreshToken> findByToken(String token);

	@Query("SELECT rt FROM RefreshToken rt WHERE rt.token = :token AND rt.isRevoked = false AND rt.expiresAt > :now")
	Optional<RefreshToken> findValidRefreshToken(@Param("token") String token, @Param("now") LocalDateTime now);

	@Modifying
	@Transactional
	@Query("UPDATE RefreshToken rt SET rt.isRevoked = true WHERE rt.user = :user")
	int revokeAllUserTokens(@Param("user") User user);
	
	@Modifying
	@Transactional
	@Query("UPDATE RefreshToken rt SET rt.isRevoked = true WHERE rt.token = :token")
	int revokeToken(@Param("token") String token);

	@Modifying
	@Transactional
	@Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :now OR rt.isRevoked = true")
	void deleteExpiredAndRevokedTokens(@Param("now") LocalDateTime now);
}