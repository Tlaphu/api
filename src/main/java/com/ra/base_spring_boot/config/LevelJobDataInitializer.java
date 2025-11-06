package com.ra.base_spring_boot.config;

import com.ra.base_spring_boot.model.LevelJob;
import com.ra.base_spring_boot.repository.LevelJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LevelJobDataInitializer implements CommandLineRunner {

    private final LevelJobRepository levelJobRepository;

    @Override
    public void run(String... args) {
        List<String> defaultLevels = Arrays.asList("INTERN", "JUNIOR", "MIDDLE", "SENIOR");

        for (String levelName : defaultLevels) {
            // Nếu chưa có thì thêm mới
            levelJobRepository.findByName(levelName).orElseGet(() -> {
                LevelJob levelJob = LevelJob.builder()
                        .name(levelName)
                        .created_at(new Date())
                        .updated_at(new Date())
                        .build();
                return levelJobRepository.save(levelJob);
            });
        }

        System.out.println(" LevelJob default data ensured: " + defaultLevels);
    }
}
