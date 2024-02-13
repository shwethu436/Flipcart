package com.jsp.fc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jsp.fc.requestDTO.AuthRequest;
import com.jsp.fc.requestDTO.OtpModel;
import com.jsp.fc.requestDTO.UserRequest;
import com.jsp.fc.responseDTO.AuthResponse;
import com.jsp.fc.responseDTO.UserResponse;
import com.jsp.fc.service.AuthService;
import com.jsp.fc.utility.ResponseStructure;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class AuthController {
	@Autowired
	private AuthService authService;
	
	@PostMapping("/register")
	public ResponseEntity<ResponseStructure<UserResponse>> registerUser(@RequestBody UserRequest request){
		return authService.registerUser(request);
		
	}
	
	@PostMapping("/verify-otp")
	public ResponseEntity<String> verifyOtp(@RequestBody OtpModel otp){
		return authService.verifyOtp(otp);
	}
	
	@PostMapping("/login")
	public ResponseEntity<ResponseStructure<AuthResponse>> login(@RequestBody AuthRequest request,HttpServletResponse response){
		return authService.login(request,response);
	}
	
	@PostMapping("/user-logout")
	public ResponseEntity<String> userLogout(@CookieValue(name = "AT",required = false) String accessToken,
			@CookieValue(name = "RT",required = false) String refreshToken,HttpServletResponse response){
		return authService.userLogout(accessToken,refreshToken,response);
	}
	
	
}
