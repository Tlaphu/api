package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.ReviewCreateRequest;
import com.ra.base_spring_boot.dto.req.ReviewUpdateRequest;
import com.ra.base_spring_boot.dto.resp.ReviewResponse;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.model.Review;
import com.ra.base_spring_boot.repository.ICandidateRepository;
import com.ra.base_spring_boot.repository.ICompanyRepository;
import com.ra.base_spring_boot.repository.ReviewRepository;
import com.ra.base_spring_boot.security.jwt.JwtProvider;
import com.ra.base_spring_boot.services.IBlacklistedWordService;
import com.ra.base_spring_boot.services.IReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import static com.ra.base_spring_boot.config.BadWordConfig.DEFAULT_BAD_WORDS;

import java.util.*;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements IReviewService {

    private final ReviewRepository reviewRepository;
    private final ICandidateRepository candidateRepository;
    private final ICompanyRepository companyRepository;
    private final JwtProvider jwtProvider;
    private final IBlacklistedWordService blacklistedWordService;

    @Override
    public List<ReviewResponse> findAll() {
        return reviewRepository.findAllByOrderByScoreDesc()
                .stream().map(this::toResponse).toList();
    }
    @Override
    public List<ReviewResponse> findAllOrderByScoreDesc() {
        return reviewRepository.findAllByOrderByScoreDesc()
                .stream().map(this::toResponse).toList();
    }

    @Override
    public ReviewResponse findById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        return toResponse(review);
    }

    @Override
    public ReviewResponse create(ReviewCreateRequest req) {
        String userType = jwtProvider.getCurrentUserType();
        Long userId = jwtProvider.getCurrentUserId();

        String detail = req.getDetail();
        if (detail != null && detail.length() > 36) {
            throw new HttpBadRequest("Review must not exceed 36 characters");
        }

        validateBlacklist(detail);

        Review review = Review.builder()
                .score(req.getScore())
                .detail(req.getDetail())
                .reviewerId(userId)
                .reviewerType(userType)
                .createdAt(new Date())
                .build();

        return toResponse(reviewRepository.save(review));
    }

    @Override
    public ReviewResponse update(Long id, ReviewUpdateRequest req) {
        Review old = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        String detail = req.getDetail();
        if (detail != null && detail.length() > 36) {
            throw new HttpBadRequest("Review must not exceed 36 characters");
        }
        validateBlacklist(detail);
        old.setScore(req.getScore());
        old.setDetail(req.getDetail());


        return toResponse(reviewRepository.save(old));
    }

    @Override
    public void delete(Long id) {
        reviewRepository.deleteById(id);
    }

    private ReviewResponse toResponse(Review r) {
        String reviewerName = null;
        String reviewerLogo = null;

        if ("CANDIDATE".equals(r.getReviewerType())) {
            var c = candidateRepository.findById(r.getReviewerId());
            if (c.isPresent()) {
                reviewerName = c.get().getName();
                reviewerLogo = c.get().getLogo();
            }
        } else if ("COMPANY".equals(r.getReviewerType())) {
            var c = companyRepository.findById(r.getReviewerId());
            if (c.isPresent()) {
                reviewerName = c.get().getName();
                reviewerLogo = c.get().getLogo();
            }
        } else if ("ACCOUNT_COMPANY".equals(r.getReviewerType())) {
            var c = companyRepository.findByAccountId(r.getReviewerId());
            if (c.isPresent()) {
                reviewerName = c.get().getName();
                reviewerLogo = c.get().getLogo();
            }
        }

        return ReviewResponse.builder()
                .id(r.getId())
                .score(r.getScore())
                .detail(r.getDetail())
                .createdAt(r.getCreatedAt())
                .reviewerName(reviewerName)
                .reviewerLogo(reviewerLogo)
                .reviewerType(r.getReviewerType())
                .build();
    }
    private void validateBlacklist(String detail) {
        if (detail == null || detail.trim().isEmpty()) return;

        // lấy từ DB nếu có, nếu không dùng DEFAULT_BAD_WORDS
        List<String> badWords = Optional.ofNullable(blacklistedWordService)
                .map(IBlacklistedWordService::findAllWords)
                .orElse(DEFAULT_BAD_WORDS);

        String normalized = normalizeText(detail);

        for (String bad : badWords) {
            if (bad == null || bad.isBlank()) continue;
            String bw = bad.toLowerCase().trim();

            // regex chống lách chữ
            StringBuilder regex = new StringBuilder();
            for (char c : bw.toCharArray()) {
                regex.append(Pattern.quote(String.valueOf(c))).append("[\\W_]*");
            }
            Pattern p = Pattern.compile(regex.toString(), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

            if (p.matcher(normalized).find() || normalized.contains(bw)) {
                throw new HttpBadRequest("Nội dung review chứa từ ngữ không phù hợp");
            }
        }
    }


    // Normalize: lowercase + remove diacritics + collapse whitespace
    private String normalizeText(String input) {
        if (input == null) return "";
        String lower = input.toLowerCase(Locale.ROOT);
        // remove diacritics (accents) to catch né dấu: "địt" -> "dit"
        String normalized = java.text.Normalizer.normalize(lower, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        // replace đ character (Vietnamese) explicitly because NFD may leave 'đ' -> 'd' works but ensure
        normalized = normalized.replace("đ", "d");
        // trim and collapse multiple whitespace
        normalized = normalized.replaceAll("\\s+", " ").trim();
        return normalized;
    }
}
