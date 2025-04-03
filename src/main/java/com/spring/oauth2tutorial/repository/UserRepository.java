package com.spring.oauth2tutorial.repository;

import com.spring.oauth2tutorial.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    public UserEntity findByUsername(String username);
}
