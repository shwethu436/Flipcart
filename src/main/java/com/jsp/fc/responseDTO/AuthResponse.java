package com.jsp.fc.responseDTO;

import com.jsp.fc.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
	private int userId;
	private String userName;
	private UserRole userRole;
	private boolean isAuthenticated;
	private long accessExpirationInSeconds;
	private long refreshExpirationInSeconds;

}
