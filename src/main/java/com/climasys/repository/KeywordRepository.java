package com.climasys.repository;

import com.climasys.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    List<Keyword> findByIsActiveTrue();

    boolean existsByKeyword(String keyword);

    boolean existsByKeywordAndIdNot(String keyword, Long id);
}
