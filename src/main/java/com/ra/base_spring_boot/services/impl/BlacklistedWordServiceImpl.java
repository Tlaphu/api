package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.model.BlacklistedWord;
import com.ra.base_spring_boot.repository.BlacklistedWordRepository;
import com.ra.base_spring_boot.services.IBlacklistedWordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlacklistedWordServiceImpl implements IBlacklistedWordService {

    private final BlacklistedWordRepository repo;

    @Override
    public List<String> findAllWords() {
        return repo.findAll().stream().map(BlacklistedWord::getWord).collect(Collectors.toList());
    }

    @Override
    public BlacklistedWord addWord(String word) {
        BlacklistedWord bw = BlacklistedWord.builder()
                .word(word.toLowerCase().trim())
                .createdAt(new Date())
                .build();
        return repo.save(bw);
    }

    @Override
    public void removeById(Long id) {
        repo.deleteById(id);
    }
}
