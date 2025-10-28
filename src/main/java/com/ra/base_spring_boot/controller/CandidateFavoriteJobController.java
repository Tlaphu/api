package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.model.Job;
import com.ra.base_spring_boot.services.ICandidateJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/candidate/favorite-jobs") // Endpoint riêng cho ứng viên
@RequiredArgsConstructor
public class CandidateFavoriteJobController {

    private final ICandidateJobService candidateJobService;

    /**
     * Lấy danh sách Job yêu thích của ứng viên hiện tại
     */
    @GetMapping
    public ResponseEntity<?> getFavoriteJobs() {
        List<Job> favoriteJobs = candidateJobService.getFavoriteJobs();
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(favoriteJobs)
                        .build()
        );
    }

    /**
     * Thêm Job vào danh sách yêu thích
     *
     * @param jobId ID của Job
     */
    @PostMapping("/{jobId}")
    public ResponseEntity<?> addFavoriteJob(@PathVariable Long jobId) {
        candidateJobService.addFavoriteJob(jobId);
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Job added to favorites successfully")
                        .build()
        );
    }

    /**
     * Xóa Job khỏi danh sách yêu thích
     *
     * @param jobId ID của Job
     */
    @DeleteMapping("/{jobId}")
    public ResponseEntity<?> removeFavoriteJob(@PathVariable Long jobId) {
        candidateJobService.removeFavoriteJob(jobId);
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Job removed from favorites successfully")
                        .build()
        );
    }

    /**
     * Kiểm tra xem Job có trong danh sách yêu thích không
     *
     * @param jobId ID của Job
     */
    @GetMapping("/check/{jobId}")
    public ResponseEntity<?> checkFavoriteJob(@PathVariable Long jobId) {
        boolean isFavorite = candidateJobService.isJobFavorite(jobId);
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(isFavorite)
                        .build()
        );
    }
}
