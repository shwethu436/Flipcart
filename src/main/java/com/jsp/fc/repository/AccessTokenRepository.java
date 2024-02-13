package com.jsp.fc.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.fc.entity.AccessToken;
import com.jsp.fc.entity.User;
import com.jsp.fc.requestDTO.UserRequest;

public interface AccessTokenRepository extends JpaRepository<AccessToken, Long> {

	Optional<AccessToken> findByToken(String accessToken);

	Optional<AccessToken> findByTokenAndIsBlocked(String token, boolean isBlocked);
	
	

	

}
