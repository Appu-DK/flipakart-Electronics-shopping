package com.flipkart.electronics_shopping.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flipkart.electronics_shopping.entity.AccessToken;

public interface AccessTokenRepo extends JpaRepository<AccessToken, Long> {

}
