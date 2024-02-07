package com.jsp.fc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jsp.fc.requestDTO.UserRequest;
import com.jsp.fc.responseDTO.UserResponse;
import com.jsp.fc.service.AuthService;
import com.jsp.fc.utility.ResponseStructure;

@RestController
public class AuthController {
	@Autowired
	private AuthService authService;
	
	@PostMapping("/register")
	public ResponseEntity<ResponseStructure<UserResponse>> registerUser(@RequestBody UserRequest request){
		return authService.registerUser(request);
		
	}

}
