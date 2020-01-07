package com.internship.amazingtaxiservice.taxiservice.repository;

import com.internship.amazingtaxiservice.taxiservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUsername(String username);

    User findByToken(String activationToken);

    User findByResetToken(String resetToken);
}