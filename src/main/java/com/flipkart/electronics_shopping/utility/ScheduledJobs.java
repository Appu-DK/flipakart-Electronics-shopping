package com.flipkart.electronics_shopping.utility;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.flipkart.electronics_shopping.entity.User;
import com.flipkart.electronics_shopping.repository.UserRepo;

@Component
public class ScheduledJobs {

	@Autowired
	private UserRepo userRepo;


	@Scheduled(cron="0 0 0 * * *")
	public void softDeleteUser() {
		List<User> users = userRepo.findByIsEmailVerified(false);
		userRepo.deleteAll(users);
	}





}
