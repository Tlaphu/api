package com.ra.base_spring_boot.config;
import com.ra.base_spring_boot.model.Candidate;
import com.ra.base_spring_boot.repository.ICandidateRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.List;
@Component
public class AccountCleanupScheduler {

    private final ICandidateRepository candidateRepository;

    // Sử dụng Dependency Injection để tiêm Repository vào
    public AccountCleanupScheduler(ICandidateRepository candidateRepository) {
        this.candidateRepository = candidateRepository;
    }

    // ⭐ TÁC VỤ ĐỊNH KỲ (SCHEDULED TASK)
    /**
     * Tác vụ này chạy mỗi 5 phút (300000ms) để xóa các tài khoản chưa kích hoạt đã quá hạn.
     */
    @Scheduled(fixedRate = 300000)
    public void cleanupExpiredUnactivatedAccounts() {
        System.out.println("Kiểm tra các tài khoản chưa kích hoạt hết hạn...");

        // Thời điểm hiện tại
        Date now = new Date();

        // 1. Sử dụng phương thức Repository vừa thêm để tìm các tài khoản hết hạn
        List<Candidate> expiredCandidates = candidateRepository
                .findByStatusFalseAndActivationExpiryDateBefore(now);

        if (!expiredCandidates.isEmpty()) {
            System.out.println("Tìm thấy " + expiredCandidates.size() + " tài khoản hết hạn và đang tiến hành xóa.");


            candidateRepository.deleteAll(expiredCandidates);

            System.out.println("Đã xóa thành công các tài khoản hết hạn.");
        } else {
            System.out.println("Không có tài khoản chưa kích hoạt nào hết hạn lúc này.");
        }
    }
}