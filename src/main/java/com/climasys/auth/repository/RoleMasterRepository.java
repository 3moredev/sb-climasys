package com.climasys.auth.repository;

import com.climasys.auth.entity.RoleMaster;
import com.climasys.auth.entity.RoleMasterId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleMasterRepository extends JpaRepository<RoleMaster, RoleMasterId> {
    
}
