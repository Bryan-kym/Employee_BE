package com.bryan.staff.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bryan.staff.domain.User;

public interface UserRepository extends JpaRepository<User, Long>{
	
	User findUserByUsername(String username);
	
	User findUserByEmail(String email);

}
