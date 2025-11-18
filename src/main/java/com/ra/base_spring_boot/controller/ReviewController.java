package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.req.ReviewCreateRequest;
import com.ra.base_spring_boot.dto.req.ReviewUpdateRequest;
import com.ra.base_spring_boot.dto.resp.ReviewResponse;
import com.ra.base_spring_boot.services.IReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final IReviewService reviewService;

    @GetMapping
    public List<ReviewResponse> getAll() {
        return reviewService.findAll();
    }

    @GetMapping("/all")
    public List<ReviewResponse> getByScoreDesc() {
        return reviewService.findAllOrderByScoreDesc();
    }

    @GetMapping("/{id}")
    public ReviewResponse getById(@PathVariable Long id) {
        return reviewService.findById(id);
    }

    @PostMapping
    public ReviewResponse create(@Valid @RequestBody ReviewCreateRequest req) {
        return reviewService.create(req);
    }

    @PutMapping("/{id}")
    public ReviewResponse update(
            @PathVariable Long id,
            @Valid @RequestBody ReviewUpdateRequest req
    ) {
        return reviewService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        reviewService.delete(id);
        return "Deleted successfully";
    }
}
