package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.model.Skill;
import java.util.List;

public interface ISkillService {
    List<Skill> findAll();

    Skill findById(Long id);

    Skill create(Skill skill);

    Skill update(Long id, Skill skill);

    void delete(Long id);
}
