package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.model.Skill;
import com.ra.base_spring_boot.repository.ISkillRepository;
import com.ra.base_spring_boot.services.ISkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements ISkillService {

    private final ISkillRepository skillRepository;

    @Override
    public List<Skill> findAll() {
        return skillRepository.findAll();
    }
}
