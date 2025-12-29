package com.climasys.service;

import com.climasys.entity.Keyword;
import com.climasys.repository.KeywordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class KeywordService {

    @Autowired
    private KeywordRepository keywordRepository;

    public List<Keyword> getAllKeywords() {
        return keywordRepository.findAll();
    }

    public List<Keyword> getActiveKeywords() {
        return keywordRepository.findByIsActiveTrue();
    }

    public Optional<Keyword> getKeywordById(Long id) {
        return keywordRepository.findById(id);
    }

    @Transactional
    public Keyword createKeyword(Keyword keyword) {
        if (keywordRepository.existsByKeyword(keyword.getKeyword())) {
            throw new IllegalArgumentException("Keyword already exists: " + keyword.getKeyword());
        }
        return keywordRepository.save(keyword);
    }

    @Transactional
    public Keyword updateKeyword(Long id, Keyword keywordDetails) {
        Keyword keyword = keywordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Keyword not found with id: " + id));

        if (keywordRepository.existsByKeywordAndIdNot(keywordDetails.getKeyword(), id)) {
            throw new IllegalArgumentException("Keyword already exists: " + keywordDetails.getKeyword());
        }

        keyword.setKeyword(keywordDetails.getKeyword());
        keyword.setDescription(keywordDetails.getDescription());
        keyword.setIsActive(keywordDetails.getIsActive());

        return keywordRepository.save(keyword);
    }

    @Transactional
    public void deleteKeyword(Long id) {
        Keyword keyword = keywordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Keyword not found with id: " + id));
        keywordRepository.delete(keyword);
    }
}
