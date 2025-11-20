package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.BlacklistedWord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlacklistedWordRepository extends JpaRepository<BlacklistedWord, Long> {
    Optional<BlacklistedWord> findByWord(String word);
}
