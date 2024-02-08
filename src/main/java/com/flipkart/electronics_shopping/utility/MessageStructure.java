package com.flipkart.electronics_shopping.utility;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageStructure {

	private String to;
	private String Subject;
	private Date sentDate;
	private String text;
	
}
