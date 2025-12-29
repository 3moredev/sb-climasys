package com.climasys.web;

import com.climasys.entity.Keyword;
import com.climasys.service.KeywordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/keywords")
public class KeywordController {

    @Autowired
    private KeywordService keywordService;

    @GetMapping
    public ResponseEntity<?> getAllKeywords() {
        try {
            return ResponseEntity.ok(keywordService.getAllKeywords());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/active")
    public ResponseEntity<List<Keyword>> getActiveKeywords() {
        return ResponseEntity.ok(keywordService.getActiveKeywords());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Keyword> getKeywordById(@PathVariable Long id) {
        return keywordService.getKeywordById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createKeyword(@RequestBody Keyword keyword) {
        try {
            Keyword createdKeyword = keywordService.createKeyword(keyword);
            return ResponseEntity.ok(createdKeyword);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateKeyword(@PathVariable Long id, @RequestBody Keyword keyword) {
        try {
            Keyword updatedKeyword = keywordService.updateKeyword(id, keyword);
            return ResponseEntity.ok(updatedKeyword);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteKeyword(@PathVariable Long id) {
        try {
            keywordService.deleteKeyword(id);
            return ResponseEntity.ok(Map.of("message", "Keyword deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
