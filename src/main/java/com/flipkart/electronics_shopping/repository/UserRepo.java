package com.flipkart.electronics_shopping.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flipkart.electronics_shopping.entity.User;


public interface UserRepo extends JpaRepository<User, Integer> {

	public boolean existsByUserEmailAndIsEmailVerified(String userEmail,boolean b);

	public boolean existsByUserEmail(String email);
}
