package com.ra.base_spring_boot.config;

import java.util.List;

public class BadWordConfig {

    // Danh sách từ cấm mặc định (fallback nếu không dùng DB)
    public static final List<String> DEFAULT_BAD_WORDS = List.of(
            "địt", "cl", "dkm", "fuck", "shit", "lồn", "ngu", "đéo"
    );

    private BadWordConfig() {
        // ngăn tạo object class này
    }
}
