package com.flipkart.electronics_shopping.security;


import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.flipkart.electronics_shopping.entity.User;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CustomUserDetail implements UserDetails {

	private User user;

	public CustomUserDetail(User user) {
		this.user=user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		return null;
	}//Collections.singleton(new SimpleGrantedAuthority(user.getUserRole().name())

	@Override
	public String getPassword() {
		//TODO Auto-generated method stub
		return user.getUserPassword();
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return user.getUserName();
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

}
