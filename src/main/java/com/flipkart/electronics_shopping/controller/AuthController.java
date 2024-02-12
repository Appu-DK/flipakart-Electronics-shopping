package com.flipkart.electronics_shopping.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flipkart.electronics_shopping.requestdto.AuthRequest;
import com.flipkart.electronics_shopping.requestdto.OtpModel;
import com.flipkart.electronics_shopping.requestdto.UserRequest;
import com.flipkart.electronics_shopping.responseddto.AuthResponse;
import com.flipkart.electronics_shopping.responseddto.UserResponse;
import com.flipkart.electronics_shopping.service.AuthService;
import com.flipkart.electronics_shopping.utility.ResponseStructure;
import com.flipkart.electronics_shopping.utility.SimpleResponseStructure;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class AuthController {

	@Autowired
	private AuthService authService;

	@PostMapping("user/register")
	public ResponseEntity<ResponseStructure<UserResponse>> register(@RequestBody UserRequest userRequest){

		return authService.register(userRequest);

	}

	@PostMapping("/verify-otp")
	public ResponseEntity<ResponseStructure<UserResponse>> verifyOTP(@RequestBody OtpModel otpModel){

		return authService.verifyOTP(otpModel);
	}

	@PostMapping("/login")
	public ResponseEntity<ResponseStructure<AuthResponse>> login(@RequestBody AuthRequest authRequest,HttpServletResponse response){
		return authService.login(authRequest,response);
	}

	@PostMapping("/logout-trade")//traditional
	public ResponseEntity<ResponseStructure<AuthResponse>> logout(HttpServletRequest request,HttpServletResponse response){
		return authService.logout(request,response);
	}
	
	@PostMapping("/logout")
	public ResponseEntity<SimpleResponseStructure> logout(@CookieValue(name="rt",required = false)String refreshToken,@CookieValue(name="at",required = false)String accessToken,HttpServletResponse response){
		
		return authService.logout(refreshToken,accessToken,response);
	}

}
