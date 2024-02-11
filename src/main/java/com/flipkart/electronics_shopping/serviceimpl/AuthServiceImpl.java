package com.flipkart.electronics_shopping.serviceimpl;

import java.time.LocalDateTime;
import java.util.Date;
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

import com.flipkart.electronics_shopping.cache.CacheStore;
import com.flipkart.electronics_shopping.entity.AccessToken;
import com.flipkart.electronics_shopping.entity.Customer;
import com.flipkart.electronics_shopping.entity.RefreshToken;
import com.flipkart.electronics_shopping.entity.Seller;
import com.flipkart.electronics_shopping.entity.User;
import com.flipkart.electronics_shopping.exception.InvalidUserRoleException;
import com.flipkart.electronics_shopping.exception.UserAlreadyRegisteredException;
import com.flipkart.electronics_shopping.repository.AccessTokenRepo;
import com.flipkart.electronics_shopping.repository.CustomerRepo;
import com.flipkart.electronics_shopping.repository.RefreshTokenRepo;
import com.flipkart.electronics_shopping.repository.SellerRepo;
import com.flipkart.electronics_shopping.repository.UserRepo;
import com.flipkart.electronics_shopping.requestdto.AuthRequest;
import com.flipkart.electronics_shopping.requestdto.OtpModel;
import com.flipkart.electronics_shopping.requestdto.UserRequest;
import com.flipkart.electronics_shopping.responseddto.AuthResponse;
import com.flipkart.electronics_shopping.responseddto.UserResponse;
import com.flipkart.electronics_shopping.security.JwtService;
import com.flipkart.electronics_shopping.service.AuthService;
import com.flipkart.electronics_shopping.utility.CookieManager;
import com.flipkart.electronics_shopping.utility.MessageStructure;
import com.flipkart.electronics_shopping.utility.ResponseStructure;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {


	private UserRepo userRepo;


	private  SellerRepo sellerRepo;


	private CustomerRepo customerRepo;

	private ResponseStructure<UserResponse> structure;

	private ResponseStructure<AuthResponse> authStructure;


	private PasswordEncoder encoder;

	private CacheStore<String> otpCacheStrore;

	private CacheStore<User> userCacheStore;

	private JavaMailSender javaMailSender;

	private AuthenticationManager authenticationManager;

	private CookieManager cookieManager;

	private JwtService jwtService;

	private AccessTokenRepo accessTokenRepo;
	private RefreshTokenRepo refreshTokenRepo;

	@Value("${myapp.access.expiry}")
	private long accessExpiryInSeconds;

	@Value("${myapp.refresh.expiry}")
	private long refreshExpiryInSeconds;

	public AuthServiceImpl(UserRepo userRepo, SellerRepo sellerRepo, CustomerRepo customerRepo,
			ResponseStructure<UserResponse> structure,ResponseStructure<AuthResponse> authStructure, PasswordEncoder encoder, CacheStore<String> otpCacheStrore,
			CacheStore<User> userCacheStore, JavaMailSender javaMailSender, AuthenticationManager authenticationManager,
			CookieManager cookieManager,JwtService jwtService,AccessTokenRepo accessTokenRepo,RefreshTokenRepo refreshTokenRepo) {
		this.userRepo = userRepo;
		this.sellerRepo = sellerRepo;
		this.customerRepo = customerRepo;
		this.structure = structure;
		this.authStructure=authStructure;
		this.encoder = encoder;
		this.otpCacheStrore = otpCacheStrore;
		this.userCacheStore = userCacheStore;
		this.javaMailSender = javaMailSender;
		this.authenticationManager = authenticationManager;
		this.cookieManager = cookieManager;
		this.jwtService=jwtService;
		this.accessTokenRepo=accessTokenRepo;
		this.refreshTokenRepo=refreshTokenRepo;

	}

	public <T extends User>T mapToUser(UserRequest userRequest){

		User user=null;
		switch(userRequest.getUserRole()) {

		case SELLER->{
			user=new Seller();
		}
		case CUSTOMER->{
			user=new Customer();
		}
		default->{throw new InvalidUserRoleException("user role is invalid");}

		}

		user.setUserEmail(userRequest.getUserEmail());
		user.setUserPassword(encoder.encode(userRequest.getUserPassword()));
		user.setUserRole(userRequest.getUserRole());
		return  (T) user;
	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> register(UserRequest userRequest) {
		User user=null;

		if(userRepo.existsByUserEmail(userRequest.getUserEmail())==false) {
			//				
			//							String email = userRequest.getUserEmail();
			//							String[] userName = email.split("@");
			//							user.setUserName(userName[0]);
			//							userRepo.save(user);
			String otp=generateOtp();
			user = mapToUser(userRequest);
			user.setUserName(userRequest.getUserEmail().split("@")[0]);

			userCacheStore.add(userRequest.getUserEmail(), user);
			otpCacheStrore.add(userRequest.getUserEmail(), otp);

			try {
				sendOtpToMail(user, otp);
			}catch(MessagingException e) {
				log.error("the email address doesn't exist");
			}

			return new ResponseEntity<ResponseStructure<UserResponse>>(structure.setStatus(HttpStatus.ACCEPTED.value())
					.setMessage("please verify your email id using OTP sent on email id")
					.setData(mapToUserRespone(user)),HttpStatus.ACCEPTED);

		}

		else {
			throw new UserAlreadyRegisteredException("user is  already existing with the given email id");
		}

	}

	//		if(user instanceof Seller) {
	//			Seller seller=(Seller)(user);
	//			sellerRepo.save(seller);
	//
	//		}
	//		else if(user  instanceof Customer) {
	//			Customer customer=(Customer)user;
	//			customerRepo.save(customer);
	//		}

	//		return new ResponseEntity<ResponseStructure<UserResponse>>(structure.setStatus(HttpStatus.ACCEPTED.value())
	//				.setMessage("please verify your email id using OTP sent")
	//				.setData(mapToUserRespone(user)),HttpStatus.ACCEPTED);


	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> verifyOTP(OtpModel otpModel) {
		// user and otp in cache memory
		User user=userCacheStore.get(otpModel.getEmail());
		String otp=otpCacheStrore.get(otpModel.getEmail());

		if(otp==null)
			throw new RuntimeException("otp is expired");
		if(user==null) 
			throw new RuntimeException("Registration session is expired");
		if(!otp.equals(otpModel.getOtp()))
			throw new RuntimeException("invalid otp");

		user.setEmailVerified(true);
		userRepo.save(user);

		// send confirm  email After saving to database
		try {
			confirmMail(user);
		} catch (MessagingException e) {
			
			e.printStackTrace();
		}


		return new ResponseEntity<ResponseStructure<UserResponse>>(structure.setStatus(HttpStatus.CREATED.value())
				.setData(mapToUserRespone(user)).setMessage("user saved  successfully"),HttpStatus.CREATED);

	}

	@Override
	public ResponseEntity<ResponseStructure<AuthResponse>> login(AuthRequest authRequest,HttpServletResponse response) {
		String userName = authRequest.getEmail().split("@")[0];
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userName,authRequest.getPassWord());

		Authentication authentication = authenticationManager.authenticate(token);
		if(!authentication.isAuthenticated())
			throw new UsernameNotFoundException("failed to authenticate");
		else {
			return userRepo.findByUserName(userName).map(user->{
				grantAccess(response, user);
				return ResponseEntity.ok(authStructure.setStatus(HttpStatus.OK.value())
						.setData(AuthResponse.builder()
								.userId(user.getUserId())
								.userName(userName)
								.role(user.getUserRole().name())
								.isAuthenticated(true)
								.accessExpiration(LocalDateTime.now().plusSeconds(accessExpiryInSeconds))
								.refreshExpiraton(LocalDateTime.now().plusSeconds(refreshExpiryInSeconds))
								.build())
						.setMessage(""));
			}).get();
		}
	}

	public void grantAccess(HttpServletResponse response,User user) {

		//generating access and refresh tokens
		String accessToken = jwtService.generateAccessToken(user.getUserName());
		String refreshToken=jwtService.generateRefreshToken(user.getUserName());

		// adding access and refresh tokens cookies to the response
		response.addCookie(cookieManager.configure(new Cookie("at",accessToken), accessExpiryInSeconds));
		response.addCookie(cookieManager.configure(new Cookie("rt", refreshToken),refreshExpiryInSeconds));

		//savings the access and refresh cookie into the database

		accessTokenRepo.save(AccessToken.builder()
				.accessToken(accessToken)
				.isBlocked(false)
				.expiration(LocalDateTime.now().plusSeconds(accessExpiryInSeconds))
				.build());
		refreshTokenRepo.save(RefreshToken.builder()
				.refreshToken(refreshToken)
				.isblocked(false)
				.expiration(LocalDateTime.now().plusSeconds(accessExpiryInSeconds))
				.build());

	}


	private void sendOtpToMail(User user,String otp) throws MessagingException {

		sendMail(MessageStructure.builder()
				.to(user.getUserEmail())
				.Subject("complete your registration to flipkart")
				.sentDate(new Date())
				.text("hey  "+user.getUserName()
				+"  Good to see you interested in flipkart,"
				+"complete your registration using the otp<br>"
				+"<h1>"+otp+"</h1><br>"
				+"Note:the OTP expires in 1 minute"
				+"<br><br>"
				+"with best regards<br>"
				+"Flipkart"

						).build());


	}

	@Async
	public void sendMail(MessageStructure message) throws MessagingException {

		MimeMessage mimeMessage=javaMailSender.createMimeMessage();
		MimeMessageHelper helper=new MimeMessageHelper(mimeMessage, true);

		helper.setTo(message.getTo());
		helper.setSubject(message.getSubject());
		helper.setSentDate(message.getSentDate());
		helper.setText(message.getText(),true);

		javaMailSender.send(mimeMessage);

	}

	public void  confirmMail(User user) throws MessagingException {
		sendMail(MessageStructure.builder()
				.to(user.getUserEmail())
				.Subject("registration completed")
				.sentDate(new Date())
				.text("namaste to flipkart application  "+user.getUserName())
				.build());

	}


	private String generateOtp() {
		return String.valueOf(new Random().nextInt(100000,999999));
	}


	private UserResponse mapToUserRespone(User user) {
		return UserResponse.builder()
				.userEmail(user.getUserEmail())
				.UserRole(user.getUserRole())
				.userName(user.getUserName())
				.userId(user.getUserId())
				.isDeleted(user.isDeleted())
				.isEmailVerified(user.isEmailVerified())
				.build();
	}


}