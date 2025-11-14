package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.model.Candidate;
import com.ra.base_spring_boot.model.Review;
import com.ra.base_spring_boot.repository.ICandidateRepository;
import com.ra.base_spring_boot.repository.ReviewRepository;
import com.ra.base_spring_boot.services.IReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements IReviewService {

    private final ReviewRepository reviewRepository;
    private final ICandidateRepository candidateRepository;

    @Override
    public List<Review> findAll() {
        return reviewRepository.findAllByOrderByScoreDesc();
    }


    @Override
    public Review findById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
    }

    @Override
    public Review create(Long candidateId, Review review) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        review.setCandidate(candidate);
        return reviewRepository.save(review);
    }

    @Override
    public Review update(Long id, Review review) {
        Review old = findById(id);

        old.setScore(review.getScore());
        old.setDetail(review.getDetail());

        return reviewRepository.save(old);
    }

    @Override
    public void delete(Long id) {
        reviewRepository.deleteById(id);
    }
}
