package com.flipkart.electronics_shopping.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.flipkart.electronics_shopping.entity.Customer;
import com.flipkart.electronics_shopping.entity.Seller;
import com.flipkart.electronics_shopping.entity.User;
import com.flipkart.electronics_shopping.repository.CustomerRepo;
import com.flipkart.electronics_shopping.repository.SellerRepo;
import com.flipkart.electronics_shopping.repository.UserRepo;
import com.flipkart.electronics_shopping.requestdto.UserRequest;
import com.flipkart.electronics_shopping.responseddto.UserResponse;
import com.flipkart.electronics_shopping.service.AuthService;
import com.flipkart.electronics_shopping.utility.ResponseStructure;

@Service
public class AuthServiceImpl implements AuthService {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private  SellerRepo sellerRepo;

	@Autowired
	private CustomerRepo customerRepo;

	@Autowired
	public ResponseStructure<UserResponse> structure;

	public UserResponse mapToUserRespone(User user) {
		return UserResponse.builder()
				.userEmail(user.getUserEmail())
				.UserRole(user.getUserRole())
				.userName(user.getUserName())
				.userId(user.getUserId())
				.isDeleted(user.isDeleted())
				.isEmailVerified(user.isEmailVerified())
				.build();
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
		default->{new RuntimeException();}

		}

		user.setUserEmail(userRequest.getUserEmail());
		user.setUserPassword(userRequest.getUserPassword());
		user.setUserRole(userRequest.getUserRole());
		return  (T) user;
	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> register(UserRequest userRequest) {

		User user=null;
		if(userRepo.existsByUserEmail(userRequest.getUserEmail())==false){
			user = mapToUser(userRequest);
			String email = userRequest.getUserEmail();
			String[] userName = email.split("@");
			user.setUserName(userName[0]);

			userRepo.save(user);
		}
		else {
			throw new RuntimeException();
		}

		if(user instanceof Seller) {
			Seller seller=(Seller)(user);
			sellerRepo.save(seller);

		}
		else if(user  instanceof Customer) {
			Customer customer=(Customer)user;
			customerRepo.save(customer);
		}

		return new ResponseEntity<ResponseStructure<UserResponse>>(structure.setStatus(HttpStatus.ACCEPTED.value())
				.setMessage("please verify your email id using OTP sent")
				.setData(mapToUserRespone(user)),HttpStatus.ACCEPTED);
	}

}
