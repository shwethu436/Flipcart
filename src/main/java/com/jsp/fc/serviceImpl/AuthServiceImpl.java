package com.jsp.fc.serviceImpl;


import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.jsp.fc.cache.CacheStore;
import com.jsp.fc.entity.AccessToken;
import com.jsp.fc.entity.Customer;
import com.jsp.fc.entity.RefreshToken;
import com.jsp.fc.entity.Seller;
import com.jsp.fc.entity.User;
import com.jsp.fc.exception.EmailAlreadyVerifiedException;
import com.jsp.fc.exception.InvalidOtpException;
import com.jsp.fc.exception.InvalidTokenException;
import com.jsp.fc.exception.OtpExpiredException;
import com.jsp.fc.exception.UserNameNotFoundException;
import com.jsp.fc.exception.noUserExistException;
import com.jsp.fc.exception.userNotLoggedInException;
import com.jsp.fc.repository.AccessTokenRepository;
import com.jsp.fc.repository.CustomerRepository;
import com.jsp.fc.repository.RefreshTokenRepository;
import com.jsp.fc.repository.SellerRepository;
import com.jsp.fc.repository.UserRepository;
import com.jsp.fc.requestDTO.AuthRequest;
import com.jsp.fc.requestDTO.OtpModel;
import com.jsp.fc.requestDTO.UserRequest;
import com.jsp.fc.responseDTO.AuthResponse;
import com.jsp.fc.responseDTO.UserResponse;
import com.jsp.fc.security.JwtService;
import com.jsp.fc.service.AuthService;
import com.jsp.fc.utility.CookieManager;
import com.jsp.fc.utility.MessageStructure;
import com.jsp.fc.utility.ResponseStructure;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
	public AuthServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepo, SellerRepository sellerRepo,
			CustomerRepository customerRepo, ResponseStructure<UserResponse> structure, CacheStore<User> userCacheStore,
			CacheStore<String> otpCache, JavaMailSender javaMailSender, AuthenticationManager authenticationManager,
			CookieManager cookiemanager,JwtService jwtService,RefreshTokenRepository refreshTokenRepo,AccessTokenRepository accessTokenRepo,ResponseStructure<AuthResponse> authStructure) {
		super();
		this.passwordEncoder = passwordEncoder;
		this.userRepo = userRepo;
		this.sellerRepo = sellerRepo;
		this.customerRepo = customerRepo;
		this.structure = structure;
		this.userCacheStore = userCacheStore;
		this.otpCache = otpCache;
		this.javaMailSender = javaMailSender;
		this.authenticationManager = authenticationManager;
		this.cookiemanager = cookiemanager;
		this.jwtService=jwtService;
		this.accessTokenRepo=accessTokenRepo;
		this.refreshTokenRepo=refreshTokenRepo;
		this.authStructure=authStructure;
	}

	private PasswordEncoder passwordEncoder;
	
	private UserRepository userRepo;
	private SellerRepository sellerRepo;
	private CustomerRepository customerRepo;
	private AccessTokenRepository accessTokenRepo;
	private RefreshTokenRepository refreshTokenRepo;
	private ResponseStructure<UserResponse> structure;
	private ResponseStructure<AuthResponse> authStructure;
	private CacheStore<User> userCacheStore;
	private CacheStore<String> otpCache;
	private JavaMailSender javaMailSender;
	private AuthenticationManager authenticationManager;
	private CookieManager cookiemanager;
	@Value("${myapp.access.expiry}")
	private int accessExpirationInSeconds;
	@Value("${myapp.refresh.expiry}")
	private int refreshExpirationInSeconds;
	private JwtService jwtService;

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
	         throw new EmailAlreadyVerifiedException("UserEmail is already present!");
	    }
	    String otp = generateOtp();
	    User user = mapToUser(request);
	    userCacheStore.add(request.getUserEmail(), user);
	    otpCache.add(request.getUserEmail(), otp);
	    try {
			sendOtpToMail(user,otp);
		} catch (MessagingException e) {
			
			log.error("This email Id doesn't exist");
		}
	     return new ResponseEntity<ResponseStructure<UserResponse>>(
	                    structure.setStatusCode(HttpStatus.ACCEPTED.value())
	                            .setMessage("please verify through otp sent on emailId")
	                            .setData(mapToUserResponse(user)), HttpStatus.ACCEPTED);
	        }
	

	@Override
	public ResponseEntity<String> verifyOtp(OtpModel otpModel) {
		User user =userCacheStore.get(otpModel.getUserEmail());
		String otp = otpCache.get(otpModel.getUserEmail());
		
		if(otp==null) throw new OtpExpiredException("otp expired");
		if(user==null) throw new noUserExistException("Regisration session expired");
		if(!otp.equals(otpModel.getOtp())) throw new InvalidOtpException("invalid otp");
		user.setEmailVerified(true);
		userRepo.save(user);
		try {
			sendRegistrationConfirmationMail(user);
		} catch (MessagingException e) {
			log.error("Failed to send registration confirmation email to user");
		}
		return new ResponseEntity<String>("Registration successful",HttpStatus.CREATED);
	}
	
	@Override
	public ResponseEntity<ResponseStructure<AuthResponse>> login(AuthRequest request,HttpServletResponse response) {
		String userName=request.getUserEmail().split("@")[0];
		String password= request.getUserPassword();
		 UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userName, password);
		 Authentication authentication = authenticationManager.authenticate(token);
		 if(!authentication.isAuthenticated()) {
			 throw new UsernameNotFoundException("user name aand password is incorrect");
		 }
		 else {
			return userRepo.findByUserName(userName).map(user ->{
				 grantAccess(response, user);
				 return ResponseEntity.ok(authStructure.setStatusCode(HttpStatus.OK.value())
						 .setData(null)
						 .setMessage("Login Successful"));
						 
			 }).get();
			
		 }
		
		     
	}
	
	@Override
	public ResponseEntity<String> userLogout(String accessToken, String refreshToken, HttpServletResponse response) {
		
		if(accessToken==null && refreshToken==null) throw new userNotLoggedInException("user is not logged in ,please login");
		
		accessTokenRepo.findByToken(accessToken)
        .ifPresent(token -> {
            token.setBlocked(true);
            accessTokenRepo.save(token);
        });


        response.addCookie(cookiemanager.invalidateCookie(new Cookie("accessToken", "")));
        
        refreshTokenRepo.findByToken(refreshToken)
        .ifPresent(token -> {
            token.setBlocked(true);
            refreshTokenRepo.save(token);
        });


        response.addCookie(cookiemanager.invalidateCookie(new Cookie("refreshToken", "")));
        
        return ResponseEntity.ok("Logged out successfully");
	}
	
	
	
	//===============================================================================================================

	private String generateOtp() {
		return String.valueOf(new Random().nextInt(100000,999999));
	}
	
	@Async
	private void sendMail(MessageStructure message) throws MessagingException {
		MimeMessage mimeMessage= javaMailSender.createMimeMessage();
		MimeMessageHelper helper =new MimeMessageHelper(mimeMessage,true);
		helper.setTo(message.getTo());
		helper.setSubject(message.getSubject());
		helper.setText(message.getText(),true);
		helper.setSentDate(message.getSentDate());
		
		javaMailSender.send(mimeMessage);
		
	}
	
	private void sendOtpToMail(User user,String otp) throws MessagingException {
		sendMail(MessageStructure.builder()
		.to(user.getUserEmail())
		.subject("Registration request")
		.sentDate(new Date())
		.text("welcome"+user.getUserEmail()+","+otp+"is the otp for the registration ")
		.build());
		
	}
	
	private void sendRegistrationConfirmationMail(User user) throws MessagingException {
		
		        sendMail(MessageStructure.builder()
		                .to(user.getUserEmail())
		                .subject("Registration Confirmation")
		                .sentDate(new Date())
		                .text("welcome"+user.getUserEmail()+" "+ "user registration completed successfully")
		                .build());
		     }
	
    private void grantAccess(HttpServletResponse response,User user) {
    	String AccessToken = jwtService.generateAccessToken(user.getUserName());
    	String RefreshToken = jwtService.generateRefreshToken(user.getUserName());
    	response.addCookie(cookiemanager.configure(new Cookie("AT", AccessToken),  accessExpirationInSeconds));
    	response.addCookie(cookiemanager.configure(new Cookie("RT", RefreshToken),  refreshExpirationInSeconds));
    	
    	//save access token
    	accessTokenRepo.save(com.jsp.fc.entity.AccessToken.builder()
    			.user(user)
    			.token(AccessToken)
    			.isBlocked(false)
    			.expiration(LocalDateTime.now().plusSeconds(accessExpirationInSeconds))
    			.build());
    	
    	refreshTokenRepo.save(com.jsp.fc.entity.RefreshToken.builder()
    			.user(user)
    			.token(RefreshToken)
    			.isBlocked(false)
    			.expiration(LocalDateTime.now().plusSeconds(refreshExpirationInSeconds))
    			.build());
    }
    
    public void deleteIfNotVerified() {

		List<User> users =userRepo.findByIsEmailVerified(false);
		userRepo.deleteAll(users);
	}

	


	

	

    
	
   	
	      
}



















