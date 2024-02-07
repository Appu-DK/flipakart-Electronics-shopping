package com.flipkart.electronics_shopping.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flipkart.electronics_shopping.entity.User;


public interface UserRepo extends JpaRepository<User, Integer> {

	public boolean existsByUserEmailAndIsEmailVerified(String userEmail,boolean b);

	public boolean existsByUserEmail(String email);
	
	public Optional<User>  findByUserName(String userName);
	
	public List<User>  findByIsEmailVerified(boolean b);
}
