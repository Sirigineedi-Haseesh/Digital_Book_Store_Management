package com.cognizant.bookstore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cognizant.bookstore.model.User;
@Repository
public interface UserRepository extends JpaRepository<User,Long> {

	Optional<User> findByUserName(String userName);
	
}
