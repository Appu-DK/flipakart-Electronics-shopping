package com.flipkart.electronics_shopping.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flipkart.electronics_shopping.entity.Seller;

public interface SellerRepo extends JpaRepository<Seller, Integer>{

}
