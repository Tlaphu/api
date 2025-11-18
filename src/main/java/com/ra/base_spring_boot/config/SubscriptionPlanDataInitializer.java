package com.ra.base_spring_boot.config;

import com.ra.base_spring_boot.model.SubscriptionPlan;
import com.ra.base_spring_boot.repository.SubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SubscriptionPlanDataInitializer implements CommandLineRunner {

    private final SubscriptionPlanRepository subscriptionPlanRepository;

    @Override
    public void run(String... args) {


        ensurePlanExists("VIP_M", "Candidate VIP 1 Tháng", new BigDecimal("36000.00"), 30);
        ensurePlanExists("VIP_Y", "Candidate VIP 1 Năm", new BigDecimal("360000.00"), 365);




        ensurePlanExists("VIP_C_M", "Company VIP 1 Tháng", new BigDecimal("500000.00"), 30);


        ensurePlanExists("VIP_C_Y", "Company VIP 1 Năm", new BigDecimal("5000000.00"), 365);

        System.out.println("SubscriptionPlan default data ensured: Candidate and Company plans added.");
    }


    private void ensurePlanExists(String planCode, String name, BigDecimal price, Integer durationInDays) {

        Optional<SubscriptionPlan> existingPlan = subscriptionPlanRepository.findByPlanCode(planCode);

        if (existingPlan.isEmpty()) {
            SubscriptionPlan newPlan = SubscriptionPlan.builder()
                    .planCode(planCode)
                    .name(name)
                    .price(price)
                    .durationInDays(durationInDays)
                    .build();
            subscriptionPlanRepository.save(newPlan);
            System.out.println("   -> Created plan: " + name);
        }
    }
}