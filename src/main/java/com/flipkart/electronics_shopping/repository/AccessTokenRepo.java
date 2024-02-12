package com.flipkart.electronics_shopping.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flipkart.electronics_shopping.entity.AccessToken;

public interface AccessTokenRepo extends JpaRepository<AccessToken, Long> {

	public Optional<AccessToken> findByAccessToken(String accessToken);

}
