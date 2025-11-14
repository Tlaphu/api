package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.model.Review;

import java.util.List;

public interface IReviewService {
    List<Review> findAll();
    Review findById(Long id);
    Review create(Long candidateId, Review review);
    Review update(Long id, Review review);
    void delete(Long id);
}
