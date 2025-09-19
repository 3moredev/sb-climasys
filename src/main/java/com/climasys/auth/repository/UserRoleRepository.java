package com.climasys.auth.repository;

import com.climasys.auth.entity.UserRole;
import com.climasys.auth.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
    
    List<UserRole> findByUserId(Long userId);
    
    @Query("SELECT ur FROM UserRole ur " +
           "JOIN FETCH ur.user u " +
           "JOIN FETCH ur.roleMaster rm " +
           "WHERE u.loginId = :loginId AND ur.isDefaultClinic = true")
    List<UserRole> findDefaultRolesByLoginId(@Param("loginId") String loginId);
    
    @Query("SELECT ur FROM UserRole ur " +
           "JOIN ur.user u " +
           "JOIN ur.roleMaster rm " +
           "WHERE u.loginId = :loginId")
    List<UserRole> findActiveRolesByLoginId(@Param("loginId") String loginId);
}
