package com.flipkart.electronics_shopping.requestdto;

import com.flipkart.electronics_shopping.enums.UserRole;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {
	
	private String userEmail;
	private String userPassword;
	
	private UserRole userRole;

}
