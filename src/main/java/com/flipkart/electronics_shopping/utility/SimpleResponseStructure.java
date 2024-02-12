package com.flipkart.electronics_shopping.utility;

import org.springframework.stereotype.Component;

@Component
public class SimpleResponseStructure {

	private String message;
	private int status;

	public String getMessage() {
		return message;
	}
	public SimpleResponseStructure setMessage(String message) {
		this.message = message;
		return this;
	}
	public int getStatus() {
		return status;
	}
	public SimpleResponseStructure setStatus(int status) {
		this.status = status;
		return this;
	}



}
