package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.model.Skill;
import com.ra.base_spring_boot.services.ISkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/skills")
@RequiredArgsConstructor
public class SkillController {

    private final ISkillService skillService;

    @GetMapping
    public ResponseEntity<List<Skill>> getAllSkills() {
        List<Skill> skills = skillService.findAll();
        return ResponseEntity.ok(skills);
    }
}
