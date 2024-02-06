package com.flipkart.electronics_shopping.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.flipkart.electronics_shopping.requestdto.UserRequest;
import com.flipkart.electronics_shopping.responseddto.UserResponse;
import com.flipkart.electronics_shopping.service.AuthService;
import com.flipkart.electronics_shopping.utility.ResponseStructure;

import jakarta.annotation.PostConstruct;

@RestController
public class AuthController {
	
 @Autowired
 private AuthService authService;
	
	@PostMapping("user/register")
	public ResponseEntity<ResponseStructure<UserResponse>> register(@RequestBody UserRequest userRequest){
		
		return authService.register(userRequest);
			
	}
	

}
