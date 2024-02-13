package com.jsp.fc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import com.jsp.fc.entity.User;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer>{
	boolean existsByUserEmail(String userEmail);
	User findByUserEmail(String userEmail);
	Optional<User> findByUserName(String username);
	List<User> findByIsEmailVerified(boolean b);
	

}
