package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.model.*;
import com.ra.base_spring_boot.repository.IJobCandidateRepository;
import com.ra.base_spring_boot.repository.JobRepository;
import com.ra.base_spring_boot.repository.ICandidateRepository;
import com.ra.base_spring_boot.repository.ICandidateCVRepository;
import com.ra.base_spring_boot.security.jwt.JwtProvider;
import com.ra.base_spring_boot.services.JobCandidateService;
import com.ra.base_spring_boot.dto.req.FormJobCandidate;
import com.ra.base_spring_boot.dto.resp.JobCandidateResponse;
import com.ra.base_spring_boot.dto.resp.CandidateResponse;
import com.ra.base_spring_boot.dto.resp.SkillsCandidateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class JobCandidateServiceImpl implements JobCandidateService {

    private final IJobCandidateRepository jobCandidateRepository;
    private final JobRepository jobRepository;
    private final ICandidateRepository candidateRepository;
    private final ICandidateCVRepository cvRepository;
    private final JwtProvider jwtProvider;

    @Autowired
    public JobCandidateServiceImpl(IJobCandidateRepository jobCandidateRepository,
                                   JobRepository jobRepository,
                                   ICandidateRepository candidateRepository,
                                   ICandidateCVRepository cvRepository, JwtProvider jwtProvider) {
        this.jobCandidateRepository = jobCandidateRepository;
        this.jobRepository = jobRepository;
        this.candidateRepository = candidateRepository;
        this.cvRepository = cvRepository;
        this.jwtProvider = jwtProvider;
    }

    private JobCandidate toEntity(FormJobCandidate form) {
        Job job = jobRepository.findById(form.getJobId())
                .orElseThrow(() -> new NoSuchElementException(String.format("Job not found with id: %d", form.getJobId())));

        Candidate candidate = candidateRepository.findById(form.getCandidateId())
                .orElseThrow(() -> new NoSuchElementException(String.format("Candidate not found with id: %d", form.getCandidateId())));

        CandidateCV candidateCV = null;
        if (form.getCvid() != null) {
            candidateCV = cvRepository.findById(form.getCvid())
                    .orElseThrow(() -> new NoSuchElementException(String.format("CV not found with id: %d", form.getCvid())));
        }

        return JobCandidate.builder()
                .job(job)
                .candidate(candidate)
                .candidateCV(candidateCV)
                .cover_letter(form.getCoverLetter())
                .status(form.getStatus() != null ? form.getStatus() : "APPLIED")
                .build();
    }

    private JobCandidateResponse toResponse(JobCandidate entity) {
        JobCandidateResponse response = new JobCandidateResponse();
        response.setId(entity.getId());

        if (entity.getJob() != null) {
            Job job = entity.getJob();
            response.setJobId(job.getId());
            response.setJobTitle(job.getTitle());
        }

        if (entity.getCandidate() != null) {
            Candidate candidate = entity.getCandidate();
            response.setCandidateId(candidate.getId());
            response.setCandidateName(candidate.getName());
            response.setCandidateTitle(candidate.getTitle());
        }

        if (entity.getCandidateCV() != null) {
            response.setCvId(entity.getCandidateCV().getId());
        } else {
            response.setCvId(null);
        }

        response.setCover_letter(entity.getCover_letter());
        response.setStatus(entity.getStatus());

        return response;
    }

    @Override
    public JobCandidateResponse create(FormJobCandidate form) {
        JobCandidate jobCandidateToCreate = toEntity(form);
        JobCandidate savedJobCandidate = jobCandidateRepository.save(jobCandidateToCreate);

        return toResponse(savedJobCandidate);
    }

    @Override
    public JobCandidateResponse update(Long id, FormJobCandidate form) {
        JobCandidate existingCandidate = jobCandidateRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("JobCandidate not found with id: " + id));


        if (form.getCvid() != null) {
            CandidateCV candidateCV = cvRepository.findById(form.getCvid())
                    .orElseThrow(() -> new NoSuchElementException(String.format("CV not found with id: %d", form.getCvid())));

            existingCandidate.setCandidateCV(candidateCV);
        } else {

            existingCandidate.setCandidateCV(null);
        }

        existingCandidate.setCover_letter(form.getCoverLetter());
        existingCandidate.setStatus(form.getStatus());

        JobCandidate updatedJobCandidate = jobCandidateRepository.save(existingCandidate);

        return toResponse(updatedJobCandidate);
    }

    @Override
    public Optional<JobCandidateResponse> findById(Long id) {
        return jobCandidateRepository.findById(id).map(this::toResponse);
    }

    @Override
    public List<JobCandidateResponse> findAll() {
        return jobCandidateRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        if (!jobCandidateRepository.existsById(id)) {
            throw new NoSuchElementException("JobCandidate not found with id: " + id);
        }

        jobCandidateRepository.deleteById(id);
    }

    @Override
    public List<JobCandidateResponse> findByJobId(Long jobId) {
        return jobCandidateRepository.findByJobId(jobId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobCandidateResponse> findByCandidateId(Long candidateId) {
        return jobCandidateRepository.findByCandidateId(candidateId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

}