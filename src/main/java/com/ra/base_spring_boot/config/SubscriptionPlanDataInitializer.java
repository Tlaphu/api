package com.ra.base_spring_boot.config;

import com.ra.base_spring_boot.model.SubscriptionPlan;
import com.ra.base_spring_boot.repository.SubscriptionPlanRepository; // Import Repository
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

        // --- Gói 1: VIP 1 Tháng ---
        ensurePlanExists("VIP_M", "Gói VIP 1 Tháng", new BigDecimal("36000.00"), 30);

        // --- Gói 2: VIP 1 Năm ---
        ensurePlanExists("VIP_Y", "Gói VIP 1 Năm", new BigDecimal("360000.00"), 365);

        System.out.println("✅ SubscriptionPlan default data ensured: VIP_M and VIP_Y");
    }

    /**
     * Hàm kiểm tra và tạo mới gói dịch vụ nếu chưa tồn tại (dựa trên planCode).
     */
    private void ensurePlanExists(String planCode, String name, BigDecimal price, Integer durationInDays) {

        // 1. Kiểm tra xem gói dịch vụ đã tồn tại chưa (Giả định bạn có hàm findByPlanCode trong Repository)
        // Nếu chưa có hàm đó, ta sẽ phải dùng findById hoặc findAll() và duyệt qua.

        Optional<SubscriptionPlan> existingPlan = subscriptionPlanRepository.findByPlanCode(planCode);

        // 2. Nếu chưa có, tạo mới và lưu vào DB
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