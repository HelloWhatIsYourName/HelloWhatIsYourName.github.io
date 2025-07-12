package com.dataglove.web.repository;

import com.dataglove.web.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByPhone(String phone);
    
    Boolean existsByUsername(String username);
    
    Boolean existsByEmail(String email);
    
    Boolean existsByPhone(String phone);
    
    @Query("SELECT u FROM User u WHERE u.status = :status")
    Page<User> findByStatus(@Param("status") User.UserStatus status, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword% OR u.email LIKE %:keyword% OR u.realName LIKE %:keyword%")
    Page<User> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}