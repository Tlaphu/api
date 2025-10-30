package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.model.Candidate;
import com.ra.base_spring_boot.model.Job;
import com.ra.base_spring_boot.model.JobCandidate;
import com.ra.base_spring_boot.repository.IJobCandidateRepository;
import com.ra.base_spring_boot.repository.JobRepository;
import com.ra.base_spring_boot.repository.ICandidateRepository;
import com.ra.base_spring_boot.services.JobCandidateService;
import com.ra.base_spring_boot.dto.req.FormJobCandidate;
import com.ra.base_spring_boot.dto.resp.JobCandidateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobCandidateServiceImpl implements JobCandidateService {

    private final IJobCandidateRepository jobCandidateRepository;
    private final JobRepository jobRepository;
    private final ICandidateRepository candidateRepository;

    @Autowired
    public JobCandidateServiceImpl(IJobCandidateRepository jobCandidateRepository,
            JobRepository jobRepository,
            ICandidateRepository candidateRepository) {
        this.jobCandidateRepository = jobCandidateRepository;
        this.jobRepository = jobRepository;
        this.candidateRepository = candidateRepository;
    }

    private JobCandidate toEntity(FormJobCandidate form) {
        Job job = jobRepository.findById(form.getJobId())
                .orElseThrow(() -> new RuntimeException(String.format("Job not found with id: %d", form.getJobId())));

        Candidate candidate = candidateRepository.findById(form.getCandidateId())
                .orElseThrow(() -> new RuntimeException(String.format("Candidate not found with id: %d", form.getCandidateId())));

        return JobCandidate.builder()
                .job(job)
                .candidate(candidate)
                .cv_url(form.getCvUrl())
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
        }

        response.setCv_url(entity.getCv_url());
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
                .orElseThrow(() -> new RuntimeException("JobCandidate not found with id: " + id));

        existingCandidate.setCv_url(form.getCvUrl());
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
            throw new RuntimeException("JobCandidate not found with id: " + id);
        }

        jobCandidateRepository.findById(id).ifPresent(jobCandidateRepository::delete);
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
