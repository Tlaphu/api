package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.model.Skill;
import java.util.List;

public interface ISkillService {
    List<Skill> findAll();
}
