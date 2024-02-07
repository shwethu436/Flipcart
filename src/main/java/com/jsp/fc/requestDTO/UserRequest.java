package com.jsp.fc.requestDTO;

import com.jsp.fc.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
	
	private String userEmail;
	private String userPassword;
	private UserRole userRole;

}
