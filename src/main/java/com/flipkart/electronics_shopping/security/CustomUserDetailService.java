package com.flipkart.electronics_shopping.security;

import java.nio.file.attribute.UserPrincipalNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.flipkart.electronics_shopping.repository.UserRepo;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CustomUserDetailService implements UserDetailsService{
	

	@Autowired
	private UserRepo userRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		return userRepo.findByUserName(username).map(user->
			new CustomUserDetail(user))
				.orElseThrow(()->new  UsernameNotFoundException("failed to authenticate user"));
	}

}
