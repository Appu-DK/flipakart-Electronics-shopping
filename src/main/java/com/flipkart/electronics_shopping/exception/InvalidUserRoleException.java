package com.flipkart.electronics_shopping.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public class InvalidUserRoleException extends RuntimeException  {

	private String message;
}
