package com.flipkart.electronics_shopping.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserAlreadyRegisteredException extends RuntimeException {

	private String message;
}
