package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.model.Candidate;
import com.ra.base_spring_boot.model.Job;
import com.ra.base_spring_boot.repository.ICandidateRepository;
import com.ra.base_spring_boot.repository.JobRepository;
import com.ra.base_spring_boot.security.jwt.JwtProvider;
import com.ra.base_spring_boot.services.ICandidateJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateJobServiceImpl implements ICandidateJobService {

    private final ICandidateRepository candidateRepository;
    private final JobRepository jobRepository;
    private final JwtProvider jwtProvider;

    private Job getJobById(Long jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new HttpBadRequest("Job not found with ID: " + jobId));
    }

    @Override
    @Transactional
    public void addFavoriteJob(Long jobId) {
        String candidateEmail = jwtProvider.getCandidateUsername();
        Candidate candidate = candidateRepository.findByEmailWithFavoriteJobs(candidateEmail)
                .orElseThrow(() -> new HttpBadRequest("Candidate not found for current user."));

        Job job = getJobById(jobId);

        if (candidate.getFavoriteJobs().contains(job)) {
            throw new HttpBadRequest("Job is already in favorites.");
        }

        candidate.getFavoriteJobs().add(job);
        candidateRepository.save(candidate);
    }

    @Override
    @Transactional
    public void removeFavoriteJob(Long jobId) {
        String candidateEmail = jwtProvider.getCandidateUsername();
        Candidate candidate = candidateRepository.findByEmailWithFavoriteJobs(candidateEmail)
                .orElseThrow(() -> new HttpBadRequest("Candidate not found for current user."));

        Job job = getJobById(jobId);

        if (!candidate.getFavoriteJobs().contains(job)) {
            throw new HttpBadRequest("Job is not in favorites.");
        }

        candidate.getFavoriteJobs().remove(job);
        candidateRepository.save(candidate);
    }

    @Override
    public List<Job> getFavoriteJobs() {
        String candidateEmail = jwtProvider.getCandidateUsername();
        Candidate candidate = candidateRepository.findByEmailWithFavoriteJobs(candidateEmail)
                .orElseThrow(() -> new HttpBadRequest("Candidate not found for current user."));

        return candidate.getFavoriteJobs().stream()
                .collect(Collectors.toList());
    }

    @Override
    public boolean isJobFavorite(Long jobId) {
        String candidateEmail = jwtProvider.getCandidateUsername();
        Candidate candidate = candidateRepository.findByEmailWithFavoriteJobs(candidateEmail)
                .orElseThrow(() -> new HttpBadRequest("Candidate not found for current user."));

        Job job = jobRepository.findById(jobId).orElse(null);

        if (job == null) {
            return false;
        }

        return candidate.getFavoriteJobs().contains(job);
    }
}
