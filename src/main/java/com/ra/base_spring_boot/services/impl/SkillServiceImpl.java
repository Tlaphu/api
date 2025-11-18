package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.model.Skill;
import com.ra.base_spring_boot.repository.ISkillRepository;
import com.ra.base_spring_boot.services.ISkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements ISkillService {

    private final ISkillRepository skillRepository;

    @Override
    public List<Skill> findAll() {
        return skillRepository.findAll();
    }
    @Override
    public Skill findById(Long id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill not found"));
    }

    @Override
    public Skill create(Skill skill) {

        if (skillRepository.existsByName(skill.getName())) {
            throw new RuntimeException("Skill already exists");
        }

        skill.setCreatedAt(new Date());
        skill.setUpdatedAt(new Date());

        return skillRepository.save(skill);
    }

    @Override
    public Skill update(Long id, Skill skill) {

        Skill existing = skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        existing.setName(skill.getName());
        existing.setUpdatedAt(new Date());

        return skillRepository.save(existing);
    }

    @Override
    public void delete(Long id) {

        Skill existing = skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        skillRepository.delete(existing);
    }
}
