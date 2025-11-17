package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.exception.HttpBadRequest;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException; // ✨ IMPORT MỚI ✨

import java.text.SimpleDateFormat;
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
                                   ICandidateCVRepository cvRepository,
                                   JwtProvider jwtProvider) {
        this.jobCandidateRepository = jobCandidateRepository;
        this.jobRepository = jobRepository;
        this.candidateRepository = candidateRepository;
        this.cvRepository = cvRepository;
        this.jwtProvider = jwtProvider;
    }

    // --- HÀM TIỆN ÍCH CHO LOGIC GIỚI HẠN (CẦN THIẾT) ---

    private Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private Date getStartOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private Date addDaysToDate(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }

    // ----------------------------------------------------

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
                .isAccepted(null)
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
            response.setCandidateAddress(candidate.getAddress());

            Set<SkillsCandidate> skills = candidate.getSkillCandidates();
            if (skills != null && !skills.isEmpty()) {
                Optional<SkillsCandidate> firstSkill = skills.stream().findFirst();
                firstSkill.ifPresent(sc -> response.setSkillcandidateId(sc.getId()));
            } else {
                response.setSkillcandidateId(null);
            }
        }

        if (entity.getCandidateCV() != null) {
            response.setCvId(entity.getCandidateCV().getId());
        } else {
            response.setCvId(null);
        }

        response.setCover_letter(entity.getCover_letter());
        response.setStatus(entity.getStatus());
        response.setIsAccepted(entity.getIsAccepted());

        return response;
    }

    // Trong JobCandidateServiceImpl.java

    @Override
    @Transactional
    public JobCandidateResponse create(FormJobCandidate form) {

        // 1. KIỂM TRA TỒN TẠI ỨNG VIÊN
        Candidate candidate = candidateRepository.findById(form.getCandidateId())
                .orElseThrow(() -> new NoSuchElementException(String.format("Candidate not found with id: %d", form.getCandidateId())));

        final int MAX_MONTHLY_APPLICATIONS = 5;
        final int LOCK_PERIOD_DAYS = 30;


        if (!candidate.isPremium()) {
            Date today = new Date();
            Date lockUntilDate = candidate.getPremiumUntil();


            if (lockUntilDate != null && lockUntilDate.after(today)) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String resetDateStr = sdf.format(lockUntilDate);
                throw new IllegalArgumentException("The account is usually locked for submission. You will be able to re-apply on the date" + resetDateStr + ".");
            }


            Date startOfMonth = getStartOfMonth(today);

            Calendar endCal = Calendar.getInstance();
            endCal.setTime(today);
            endCal.add(Calendar.DAY_OF_MONTH, 1);
            Date endDateForQuery = endCal.getTime();

            Integer totalMonthlyApplications = jobCandidateRepository.countApplicationsInMonth(
                    candidate.getId(),
                    startOfMonth,
                    endDateForQuery
            );

            if (totalMonthlyApplications == null) {
                totalMonthlyApplications = 0;
            }


            if (totalMonthlyApplications >= MAX_MONTHLY_APPLICATIONS) {

                Date newLockDate = addDaysToDate(today, LOCK_PERIOD_DAYS);

                candidate.setPremiumUntil(newLockDate);
                candidateRepository.save(candidate);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String lockDateStr = sdf.format(newLockDate);

                throw new IllegalArgumentException("Regular account has reached limit " + MAX_MONTHLY_APPLICATIONS + " đơn nộp trong tháng. Bạn sẽ bị khóa nộp đơn cho đến hết ngày " + lockDateStr + ".");
            }
        }


        // 2. KIỂM TRA TÌNH TRẠNG ĐƠN ỨNG TUYỂN TRƯỚC (NỘP LẠI HAY BỊ CHẶN)
        Optional<JobCandidate> existingApplicationOpt = jobCandidateRepository
                .findByJobIdAndCandidateId(form.getJobId(), form.getCandidateId());

        if (existingApplicationOpt.isPresent()) {
            JobCandidate existingApplication = existingApplicationOpt.get();

            // Chặn nếu đơn trước đó ĐANG CHỜ XỬ LÝ (null) hoặc ĐÃ CHẤP NHẬN (true)
            if (existingApplication.getIsAccepted() == null || existingApplication.getIsAccepted() == true) {
                throw new IllegalArgumentException("You have already applied to this job, and the current application status is not rejected.");
            }

            // Nếu isAccepted = false (ĐÃ BỊ TỪ CHỐI): Cập nhật lại đơn cũ thành đơn mới
            if (existingApplication.getIsAccepted() != null && existingApplication.getIsAccepted() == false) {

                // Cập nhật lại thông tin mới
                existingApplication.setCandidateCV(form.getCvid() != null ?
                        cvRepository.findById(form.getCvid()).orElse(null) : null);
                existingApplication.setCover_letter(form.getCoverLetter());

                // Đặt lại trạng thái chờ xử lý
                existingApplication.setStatus(form.getStatus() != null ? form.getStatus() : "REAPPLIED");
                existingApplication.setIsAccepted(null);

                JobCandidate reAppliedJobCandidate = jobCandidateRepository.save(existingApplication);
                return toResponse(reAppliedJobCandidate);
            }
        }


        // 3. NỘP ĐƠN LẦN ĐẦU (Hoặc tạo đơn mới nếu logic nộp lại ở trên không chạy)
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

        if (form.getStatus() != null) {
            existingCandidate.setStatus(form.getStatus());
        }

        // Cập nhật trạng thái chấp nhận/từ chối
        if (form.getIsAccepted() != null) {
            existingCandidate.setIsAccepted(form.getIsAccepted());
        }

        JobCandidate updatedJobCandidate = jobCandidateRepository.save(existingCandidate);

        return toResponse(updatedJobCandidate);
    }

    @Override
    @Transactional
    public JobCandidateResponse setAcceptanceStatus(Long id, Boolean isAccepted) {

        JobCandidate existingCandidate = jobCandidateRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("JobCandidate not found with id: " + id));

        AccountCompany currentCompany = jwtProvider.getCurrentAccountCompany();

        // 1. KIỂM TRA QUYỀN SỞ HỮU
        if (currentCompany == null ||
                !existingCandidate.getJob().getCompany().getId().equals(currentCompany.getId())) {

            // ✨ Ném AccessDeniedException thay vì SecurityException ✨
            throw new AccessDeniedException("You do not have permission to modify the status of this job application.");
        }

        // 2. Cập nhật trạng thái
        existingCandidate.setIsAccepted(isAccepted);
        // Tùy chọn: Cập nhật status dựa trên kết quả
        existingCandidate.setStatus(isAccepted ? "ACCEPTED" : "REJECTED");

        JobCandidate updatedJobCandidate = jobCandidateRepository.save(existingCandidate);

        return toResponse(updatedJobCandidate);
    }

    // ... (Các phương thức khác) ...

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

    @Override
    public List<CandidateResponse> getSuitableCandidatesForCompanyJob(Long jobId) {

        AccountCompany company = jwtProvider.getCurrentAccountCompany();
        if (company == null) {
            throw new NoSuchElementException("Unauthorized: Company not found or token invalid");
        }

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new NoSuchElementException("Job not found with id: " + jobId));

        // Kiểm tra quyền sở hữu công việc này (giống logic ở setAcceptanceStatus)
        if (!job.getCompany().getId().equals(company.getId())) {
            // ✨ Ném AccessDeniedException thay vì SecurityException ✨
            throw new AccessDeniedException("You are not allowed to access this job's candidates.");
        }

        Set<Skill> requiredSkills = job.getSkills();
        if (requiredSkills == null || requiredSkills.isEmpty()) {
            throw new NoSuchElementException("Job does not have any required skills");
        }

        List<Candidate> candidates = candidateRepository.findAll()
                .stream()
                .filter(Candidate::isStatus)
                .toList();

        List<Candidate> suitable = candidates.stream()
                .filter(c -> c.getSkillCandidates() != null && c.getSkillCandidates().stream()
                        .anyMatch(sc -> requiredSkills.stream()
                                .anyMatch(rs -> rs.getName().equalsIgnoreCase(sc.getSkill().getName()))))
                .collect(Collectors.toList());

        suitable.sort(Comparator.comparingInt(c -> {
            Optional<SkillsCandidate> topLevel = c.getSkillCandidates().stream()
                    .max(Comparator.comparingInt(sc -> levelRank(sc.getLevelJob())));
            return -topLevel.map(sc -> levelRank(sc.getLevelJob())).orElse(0);
        }));

        return suitable.stream()
                .map(c -> CandidateResponse.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .email(c.getEmail())
                        .phone(c.getPhone())
                        .Title(c.getTitle())
                        .description(c.getDescription())
                        .address(c.getAddress())
                        .skills(c.getSkillCandidates().stream()
                                .map(s -> SkillsCandidateResponse.builder()
                                        .id(s.getId())
                                        .skillName(s.getSkill().getName())
                                        .levelJobName(s.getLevelJob().getName())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    private int levelRank(LevelJob levelJob) {
        if (levelJob == null || levelJob.getName() == null) return 0;
        return switch (levelJob.getName().toUpperCase()) {
            case "JUNIOR" -> 1;
            case "MIDDLE" -> 2;
            case "SENIOR" -> 3;
            case "INTERN" -> 4;
            default -> 0;
        };
    }

    @Override
    @Transactional
    public void deleteByJobId(Long jobId) {
        jobCandidateRepository.deleteByJobId(jobId);
    }

}