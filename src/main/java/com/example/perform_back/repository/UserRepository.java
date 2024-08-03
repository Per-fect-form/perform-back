package com.example.perform_back.repository;

import com.example.perform_back.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.ad = true AND u.isExpert = true")
    List<User> findAllExpertsForAd();
}
