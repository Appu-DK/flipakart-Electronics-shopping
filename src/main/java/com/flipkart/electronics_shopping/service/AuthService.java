package com.flipkart.electronics_shopping.service;

import org.springframework.http.ResponseEntity;

import com.flipkart.electronics_shopping.requestdto.UserRequest;
import com.flipkart.electronics_shopping.responseddto.UserResponse;
import com.flipkart.electronics_shopping.utility.ResponseStructure;

public interface AuthService {

	ResponseEntity<ResponseStructure<UserResponse>> register(UserRequest userRequest);

}
