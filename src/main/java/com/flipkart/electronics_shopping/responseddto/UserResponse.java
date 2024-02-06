package com.flipkart.electronics_shopping.responseddto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ Builder
public class UserResponse {

	private String userName;
	private String userEmail;
	private int userId;
	private Enum UserRole;
	private boolean isEmailVerified;
	private boolean isDeleted;

}
