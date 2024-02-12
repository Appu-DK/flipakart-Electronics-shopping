package com.flipkart.electronics_shopping.service;

import org.springframework.http.ResponseEntity;

import com.flipkart.electronics_shopping.requestdto.AuthRequest;
import com.flipkart.electronics_shopping.requestdto.OtpModel;
import com.flipkart.electronics_shopping.requestdto.UserRequest;
import com.flipkart.electronics_shopping.responseddto.AuthResponse;
import com.flipkart.electronics_shopping.responseddto.UserResponse;
import com.flipkart.electronics_shopping.utility.ResponseStructure;
import com.flipkart.electronics_shopping.utility.SimpleResponseStructure;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

	ResponseEntity<ResponseStructure<UserResponse>> register(UserRequest userRequest);

	ResponseEntity<ResponseStructure<UserResponse>> verifyOTP(OtpModel otpModel);

	ResponseEntity<ResponseStructure<AuthResponse>> login(AuthRequest authRequest,HttpServletResponse response);

	ResponseEntity<ResponseStructure<AuthResponse>> logout(HttpServletRequest request, HttpServletResponse response);

	ResponseEntity<SimpleResponseStructure> logout(String refreshToken, String accessToken,HttpServletResponse response);

}
