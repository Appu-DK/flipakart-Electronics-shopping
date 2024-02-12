package com.flipkart.electronics_shopping.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flipkart.electronics_shopping.entity.RefreshToken;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {

	public Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
