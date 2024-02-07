package com.jsp.fc.service;

import org.springframework.http.ResponseEntity;

import com.jsp.fc.requestDTO.UserRequest;
import com.jsp.fc.responseDTO.UserResponse;
import com.jsp.fc.utility.ResponseStructure;

public interface AuthService {

	ResponseEntity<ResponseStructure<UserResponse>> registerUser(UserRequest request);

}
