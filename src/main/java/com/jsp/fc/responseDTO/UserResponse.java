package com.jsp.fc.responseDTO;

import com.jsp.fc.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
	private int userId;
	private String userName;
	private String userEmail;
	private UserRole userRole;
	private boolean isEmailVerified;
	private boolean isDeleted;

}
