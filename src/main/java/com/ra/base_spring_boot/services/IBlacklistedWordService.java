package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.model.BlacklistedWord;

import java.util.List;

public interface IBlacklistedWordService {
    List<String> findAllWords();
    BlacklistedWord addWord(String word);
    void removeById(Long id);
}
