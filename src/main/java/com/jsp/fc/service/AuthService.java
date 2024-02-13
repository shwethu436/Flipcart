package com.jsp.fc.service;

import org.springframework.http.ResponseEntity;

import com.jsp.fc.requestDTO.AuthRequest;
import com.jsp.fc.requestDTO.OtpModel;
import com.jsp.fc.requestDTO.UserRequest;
import com.jsp.fc.responseDTO.AuthResponse;
import com.jsp.fc.responseDTO.UserResponse;
import com.jsp.fc.utility.ResponseStructure;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

	ResponseEntity<ResponseStructure<UserResponse>> registerUser(UserRequest request);

	ResponseEntity<String> verifyOtp(OtpModel otp);

	ResponseEntity<ResponseStructure<AuthResponse>> login(AuthRequest request, HttpServletResponse response);

	

	ResponseEntity<String> userLogout(String accessToken, String refreshToken, HttpServletResponse response);

	ResponseEntity<String> revokeAllDevices(String accessToken, String refreshToken,HttpServletResponse response);

	ResponseEntity<String> revokeAllOtherDevices(String accessToken, String refreshToken,HttpServletResponse response);

	

}
