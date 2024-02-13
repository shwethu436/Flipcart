package com.jsp.fc.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.fc.entity.RefreshToken;
import com.jsp.fc.entity.User;
import com.jsp.fc.requestDTO.UserRequest;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	Optional<RefreshToken> findByToken(String refreshToken);
	
	
	
	

}
