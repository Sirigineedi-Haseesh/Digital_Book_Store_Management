package com.cognizant.bookstore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cognizant.bookstore.model.User;

public interface UserRepository extends JpaRepository<User,Long> {

	Optional<User> findByUserName(String userName);
	
}
