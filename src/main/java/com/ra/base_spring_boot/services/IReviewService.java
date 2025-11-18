package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.ReviewCreateRequest;
import com.ra.base_spring_boot.dto.req.ReviewUpdateRequest;
import com.ra.base_spring_boot.dto.resp.ReviewResponse;

import java.util.List;

public interface IReviewService {

    List<ReviewResponse> findAll();

    List<ReviewResponse> findAllOrderByScoreDesc();

    ReviewResponse findById(Long id);

    ReviewResponse create(ReviewCreateRequest req);

    ReviewResponse update(Long id, ReviewUpdateRequest req);

    void delete(Long id);
}
