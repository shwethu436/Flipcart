package com.jsp.fc.serviceImpl;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jsp.fc.entity.Customer;
import com.jsp.fc.entity.Seller;
import com.jsp.fc.entity.User;
import com.jsp.fc.exception.DuplicateUserEmailAndPassword;
import com.jsp.fc.exception.EmailAlreadyVerifiedException;
import com.jsp.fc.repository.CustomerRepository;
import com.jsp.fc.repository.SellerRepository;
import com.jsp.fc.repository.UserRepository;
import com.jsp.fc.requestDTO.UserRequest;
import com.jsp.fc.responseDTO.UserResponse;
import com.jsp.fc.service.AuthService;
import com.jsp.fc.utility.ResponseStructure;

import lombok.AllArgsConstructor;
@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
	private PasswordEncoder passwordEncoder;
	
	private UserRepository userRepo;
	
	private SellerRepository sellerRepo;
	
	private CustomerRepository customerRepo;
	
	private ResponseStructure<UserResponse> structure;

	private User saveUser(UserRequest request) {
		User user=null;

		switch (request.getUserRole()){
		case CUSTOMER-> {
			user=customerRepo.save(mapToUser(request));
			
		}
		case SELLER -> {
			user=sellerRepo.save(mapToUser(request));
			
		}
		default -> throw new RuntimeException();
		}
		return user;
		
	}

		public <T extends User>T mapToUser(UserRequest request) {
			User user=null;

			switch (request.getUserRole()){
			case CUSTOMER -> {
				user=new Customer();
				
			}
			case SELLER -> {
				user=new Seller();
				
			}
              default -> throw new RuntimeException();
			}
			user.setUserPassword(passwordEncoder.encode(request.getUserPassword()));
			user.setUserEmail(request.getUserEmail());
			user.setUserRole(request.getUserRole());
			user.setDeleted(false);
			user.setEmailVerified(false);
			user.setUserName(request.getUserEmail().split("@")[0]);//username will be 

			return (T)user;
		}

		public UserResponse mapToUserResponse(User user) {
			return UserResponse.builder()
					.userId(user.getUserId())
					.userName(user.getUserName())
					.userEmail(user.getUserEmail())
					.userRole(user.getUserRole())
					.isDeleted(user.isDeleted())
					.isEmailVerified(user.isEmailVerified())
					.build();
		}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> registerUser(UserRequest request) {
	   
	    if (userRepo.existsByUserEmail(request.getUserEmail())) {
	        User user = userRepo.findByUserEmail(request.getUserEmail());
	        if (user.isEmailVerified()) {
	            // If email is already verified, throw exception
	            throw new EmailAlreadyVerifiedException("UserEmail is already verified!");
	        } else {
	            // If email exists but not verified, send verification email and return response
	            sendVerificationEmail(user);
	            return new ResponseEntity<ResponseStructure<UserResponse>>(
	                    structure.setStatusCode(HttpStatus.ACCEPTED.value())
	                            .setMessage("Email verification sent. Please verify your email to complete registration.")
	                            .setData(mapToUserResponse(user)), HttpStatus.ACCEPTED);
	        }
	    } else {
	        // If email doesn't exist or not verified, save user data in the database
	        User user = saveUser(request);
	        return new ResponseEntity<ResponseStructure<UserResponse>>(
	                structure.setStatusCode(HttpStatus.ACCEPTED.value())
	                        .setMessage("User registered successfully!!").setData( mapToUserResponse(user)),
	                HttpStatus.ACCEPTED);
	    }
	}
	private void sendVerificationEmail(User user) {
	    // Send verification email logic
	}

	      
	    

}
