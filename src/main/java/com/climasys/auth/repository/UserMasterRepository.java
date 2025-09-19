package com.climasys.auth.repository;

import com.climasys.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserMasterRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByLoginIdAndPasswordAndIsActive(String loginId, String password, Boolean isActive);
    
    Optional<User> findByLoginIdAndIsActive(String loginId, Boolean isActive);
    
    @Query("SELECT u FROM User u WHERE u.loginId = :loginId AND u.password = :password AND u.isActive = true")
    Optional<User> findActiveUserByCredentials(@Param("loginId") String loginId, @Param("password") String password);
}
