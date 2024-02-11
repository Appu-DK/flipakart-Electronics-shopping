package com.flipkart.electronics_shopping.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flipkart.electronics_shopping.entity.RefreshToken;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {

}
