package com.flipkart.electronics_shopping.serviceimpl;

import java.util.Date;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.flipkart.electronics_shopping.cache.CacheBeanConfig;
import com.flipkart.electronics_shopping.cache.CacheStore;
import com.flipkart.electronics_shopping.entity.Customer;
import com.flipkart.electronics_shopping.entity.Seller;
import com.flipkart.electronics_shopping.entity.User;
import com.flipkart.electronics_shopping.exception.InvalidUserRoleException;
import com.flipkart.electronics_shopping.exception.UserAlreadyRegisteredException;
import com.flipkart.electronics_shopping.repository.CustomerRepo;
import com.flipkart.electronics_shopping.repository.SellerRepo;
import com.flipkart.electronics_shopping.repository.UserRepo;
import com.flipkart.electronics_shopping.requestdto.OtpModel;
import com.flipkart.electronics_shopping.requestdto.UserRequest;
import com.flipkart.electronics_shopping.responseddto.UserResponse;
import com.flipkart.electronics_shopping.service.AuthService;
import com.flipkart.electronics_shopping.utility.MessageStructure;
import com.flipkart.electronics_shopping.utility.ResponseStructure;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private  SellerRepo sellerRepo;

	@Autowired
	private CustomerRepo customerRepo;

	@Autowired
	private ResponseStructure<UserResponse> structure;

	@Autowired
	private PasswordEncoder encoder;

	private CacheStore<String> otpCacheStrore;

	private CacheStore<User> userCacheStore;

	private JavaMailSender javaMailSender;



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
		
		// send to  email After saving to database
		confirmMail(user);


		return new ResponseEntity<ResponseStructure<UserResponse>>(structure.setStatus(HttpStatus.CREATED.value())
				.setData(mapToUserRespone(user)).setMessage("user saved  successfully"),HttpStatus.CREATED);

	}

	private void sendOtpToMail(User user,String otp) throws MessagingException {

		sendMail(MessageStructure.builder()
				.to(user.getUserEmail())
				.Subject("complete your registration to flipkart")
				.sentDate(new Date())
				.text("hey"+user.getUserName()
				+"Good to see you interested in flipkart,"
				+"complete your regostartion using the otp<br>"
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
	
	public void  confirmMail(User user) {
		MessageStructure.builder()
		.to(user.getUserEmail())
		.Subject("registration completed")
		.sentDate(new Date())
		.text("namaste to flipkart application"+user.getUserName())
		.build();
		
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