package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.model.Review;
import com.ra.base_spring_boot.services.IReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final IReviewService reviewService;

    @GetMapping
    public List<Review> getAll() {
        return reviewService.findAll();
    }

    @GetMapping("/{id}")
    public Review findById(@PathVariable Long id) {
        return reviewService.findById(id);
    }

    @PostMapping("/candidate/{candidateId}")
    public Review create(@PathVariable Long candidateId, @RequestBody Review review) {
        return reviewService.create(candidateId, review);
    }

    @PutMapping("/{id}")
    public Review update(@PathVariable Long id, @RequestBody Review review) {
        return reviewService.update(id, review);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        reviewService.delete(id);
        return "Delete successfully";
    }
}
