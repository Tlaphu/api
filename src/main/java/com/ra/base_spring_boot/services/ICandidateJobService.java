package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.model.Job; 
import java.util.List;

public interface ICandidateJobService {
    
    /**
     * Thêm Job vào danh sách yêu thích của Candidate hiện tại
     * @param jobId ID của Job cần thêm
     */
    void addFavoriteJob(Long jobId);

    /**
     * Xóa Job khỏi danh sách yêu thích của Candidate hiện tại
     * @param jobId ID của Job cần xóa
     */
    void removeFavoriteJob(Long jobId);

    /**
     * Lấy danh sách Job yêu thích của Candidate hiện tại
     * @return List các Job yêu thích
     */
    List<Job> getFavoriteJobs();

    /**
     * Kiểm tra xem một Job có phải là yêu thích của Candidate hiện tại không
     * @param jobId ID của Job cần kiểm tra
     * @return true nếu là yêu thích, false nếu ngược lại
     */
    boolean isJobFavorite(Long jobId);
}