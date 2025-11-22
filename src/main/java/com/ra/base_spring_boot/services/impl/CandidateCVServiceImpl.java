package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.*;
import com.ra.base_spring_boot.dto.resp.CandidateCVResponse;
import com.ra.base_spring_boot.exception.HttpBadRequest;

import com.ra.base_spring_boot.model.*;
import com.ra.base_spring_boot.repository.*;
import com.ra.base_spring_boot.services.ICandidateCVService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.File;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import com.ra.base_spring_boot.exception.HttpForbiden;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

// THYMELAFF IMPORTS
import org.springframework.beans.factory.annotation.Qualifier;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateInputException; // Import mới cho xử lý lỗi

@Service
@RequiredArgsConstructor
public class CandidateCVServiceImpl implements ICandidateCVService {

    private final ICandidateRepository candidateRepository;
    private final ICandidateCVRepository candidateCVRepository;
    private final ICVCreationCountRepository cvCreationCountRepository;
    private final ICandidateCVArchiveRepository candidateCVArchiveRepository;
    private final IJobCandidateRepository jobCandidateRepository;
    private final ICandidateCVRepository cvRepository;

    // INJECT THYMELAFF TEMPLATE ENGINE
    @Qualifier("pdfTemplateEngine")
    private final TemplateEngine templateEngine;

    @Value("${file.upload.cv-dir:./uploads/cv_files/}")
    private String UPLOAD_DIR;
    private static final SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy");
    private static final SimpleDateFormat MONTH_YEAR_FORMAT = new SimpleDateFormat("MM/yyyy");

    public CandidateCVResponse mapToResponse(CandidateCV candidateCV) {
        Candidate candidate = candidateCV.getCandidate();
        final String DELIMITER = " | ";

        List<String> projectNames = splitStringToList(candidateCV.getProjectCandidateNames(), DELIMITER);
        List<String> projectLinks = splitStringToList(candidateCV.getProjectCandidateLink(), DELIMITER);
        List<String> projectInfos = splitStringToList(candidateCV.getProjectCandidateInfo(), DELIMITER);
        List<String> projectStartDates = splitStringToList(candidateCV.getProjectCandidateStartDates(), DELIMITER);
        List<String> projectEndDates = splitStringToList(candidateCV.getProjectCandidateEndDates(), DELIMITER);

        List<String> skillNames = splitStringToList(candidateCV.getSkillCandidateNames(), DELIMITER);

        List<String> educationNames = splitStringToList(candidateCV.getEducationCandidateNames(), DELIMITER);
        List<String> educationMajors = splitStringToList(candidateCV.getEducationCandidateMajor(), DELIMITER);
        List<String> educationGPAs = splitStringToList(candidateCV.getEducationCandidateGPA(), DELIMITER);
        List<String> educationInfos = splitStringToList(candidateCV.getEducationCandidateInfo(), DELIMITER);
        List<String> educationStartDates = splitStringToList(candidateCV.getEducationCandidateStartDates(), DELIMITER);
        List<String> educationEndDates = splitStringToList(candidateCV.getEducationCandidateEndDates(), DELIMITER);

        List<String> experienceNames = splitStringToList(candidateCV.getExperienceCandidateNames(), DELIMITER);
        List<String> experiencePositions = splitStringToList(candidateCV.getExperienceCandidatePosition(), DELIMITER);
        List<String> experienceCompanies = splitStringToList(candidateCV.getExperienceCandidateCompany(), DELIMITER);
        List<String> experienceInfos = splitStringToList(candidateCV.getExperienceCandidateInfo(), DELIMITER);
        List<String> experienceStartDates = splitStringToList(candidateCV.getExperienceCandidateStartDates(), DELIMITER);
        List<String> experienceEndDates = splitStringToList(candidateCV.getExperienceCandidateEndDates(), DELIMITER);

        List<String> certificateNames = splitStringToList(candidateCV.getCertificateCandidateNames(), DELIMITER);
        List<String> certificateOrganizations = splitStringToList(candidateCV.getCertificateCandidateOrganization(), DELIMITER);
        List<String> certificateInfos = splitStringToList(candidateCV.getCertificateCandidateInfo(), DELIMITER);
        List<String> certificateStartDates = splitStringToList(candidateCV.getCertificateCandidateStartDates(), DELIMITER);
        List<String> certificateEndDates = splitStringToList(candidateCV.getCertificateCandidateEndDates(), DELIMITER);

        return CandidateCVResponse.builder()
                .id(candidateCV.getId())
                .name(candidateCV.getName())
                .dob(candidateCV.getDob())
                .address(candidateCV.getAddress())
                .title(candidateCV.getTitle())
                .template(candidateCV.getTemplate())

                .gender(candidateCV.getGender())
                .link(candidate.getLink())
                .description(candidate.getDescription())
                .development(candidate.getDevelopment())
                .candidateTitle(candidateCV.getCandidateTitle())

                .email(candidateCV.getEmail())
                .phone(candidateCV.getPhone())
                .avatar(candidateCV.getAvatar())
                .hobbies(candidateCV.getHobbies())

                .projects(projectNames)
                .projectLinks(projectLinks)
                .projectInfos(projectInfos)
                .projectStartDates(projectStartDates)
                .projectEndDates(projectEndDates)

                .skills(skillNames)

                .educations(educationNames)
                .educationMajors(educationMajors)
                .educationGPAs(educationGPAs)
                .educationInfos(educationInfos)
                .educationStartDates(educationStartDates)
                .educationEndDates(educationEndDates)

                .experiences(experienceNames)
                .experiencePositions(experiencePositions)
                .experienceCompanies(experienceCompanies)
                .experienceInfos(experienceInfos)
                .experienceStartDates(experienceStartDates)
                .experienceEndDates(experienceEndDates)

                .certificates(certificateNames)
                .certificateOrganizations(certificateOrganizations)
                .certificateInfos(certificateInfos)
                .certificateStartDates(certificateStartDates)
                .certificateEndDates(certificateEndDates)

                .build();
    }

    private List<String> splitStringToList(String data, String delimiter) {
        if (data == null || data.trim().isEmpty()) {
            return List.of();
        }

        String regexDelimiter = delimiter.replace("|", "\\|");

        return List.of(data.trim().split(regexDelimiter)).stream()
                .map(String::trim)
                .collect(Collectors.toList());
    }

    private Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
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

    private void mapPersonalInfo(CandidateCV cvEntity, FormCandidateCV cvForm, Candidate candidate) {
        cvEntity.setName(cvForm.getName() != null ? cvForm.getName() : candidate.getName());
        cvEntity.setGender(cvForm.getGender() != null ? cvForm.getGender() : candidate.getGender());
        cvEntity.setDob(cvForm.getDob() != null ? cvForm.getDob() : candidate.getDob());
        cvEntity.setEmail(cvForm.getEmail() != null ? cvForm.getEmail() : candidate.getEmail());
        cvEntity.setPhone(cvForm.getPhone() != null ? cvForm.getPhone() : candidate.getPhone());
        cvEntity.setAddress(cvForm.getAddress() != null ? cvForm.getAddress() : candidate.getAddress());
        cvEntity.setLink(cvForm.getLink() != null ? cvForm.getLink() : candidate.getLink());
        cvEntity.setDescription(cvForm.getDescription() != null ? cvForm.getDescription() : candidate.getDescription());
        cvEntity.setDevelopment(cvForm.getDevelopment() != null ? cvForm.getDevelopment() : candidate.getTitle());
        cvEntity.setCandidateTitle(cvForm.getCandidateTitle() != null ? cvForm.getCandidateTitle() : candidate.getTitle());
        cvEntity.setHobbies(cvForm.getHobbies()!= null ? cvForm.getHobbies() : cvEntity.getHobbies());
        cvEntity.setAvatar(cvForm.getAvatar() != null ? cvForm.getAvatar() : cvEntity.getAvatar());
    }
    @Override
    @Transactional
    public CandidateCV setCvPublicStatus(Long cvId, Long candidateId, Boolean isPublic) {
        CandidateCV existingCV = candidateCVRepository.findByIdAndCandidate_Id(cvId, candidateId)
                .orElseThrow(() -> new HttpBadRequest("CV not found or does not belong to this candidate."));

        existingCV.setIsPublic(isPublic);

        return candidateCVRepository.save(existingCV);
    }

    @Override
    public byte[] downloadCvForCompany(Long cvId, Long companyId) {

        CandidateCV cvEntity = candidateCVRepository.findById(cvId)
                .orElseThrow(() -> new NoSuchElementException("CV not found with id: " + cvId));

        List<JobCandidate> applications = jobCandidateRepository.findByCandidateCVId(cvId);

        boolean hasPermission = applications.stream()
                .anyMatch(app -> app.getJob().getCompany().getId().equals(companyId));

        if (!hasPermission) {
            throw new HttpForbiden("Access Denied: This CV is not associated with any of your company's job applications.");
        }

        if (cvEntity.getIs_upload_file() != null && !cvEntity.getIs_upload_file()) {

            return generatePdfFromCV(cvId, cvEntity.getCandidate().getId());

        } else {
            String filePathString = cvEntity.getFile_cv();
            if (filePathString == null || filePathString.isEmpty()) {
                throw new HttpBadRequest("File CV path is missing. Cannot retrieve uploaded file.");
            }

            try {
                Path filePath = Paths.get(filePathString);

                if (!Files.exists(filePath)) {
                    filePath = Paths.get(UPLOAD_DIR, filePathString);
                    if (!Files.exists(filePath)) {
                        throw new IOException("CV file not found (Check if file path in DB is correct: " + filePathString + ")");
                    }
                }

                return Files.readAllBytes(filePath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to read CV file: " + e.getMessage(), e);
            }
        }
    }

    @Override
    @Transactional
    public CandidateCV createNewCV(FormCandidateCV cvForm, Long candidateId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new HttpBadRequest("Candidate not found with ID: " + candidateId));

        final int MAX_CVS_COUNT = 5;
        final int LOCK_PERIOD_DAYS = 30;

        if (!candidate.isPremium()) {
            Date today = new Date();
            Date lockUntilDate = candidate.getPremiumUntil();

            if (lockUntilDate != null && lockUntilDate.after(today)) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String resetDateStr = sdf.format(lockUntilDate);
                throw new HttpBadRequest("Regular accounts have reached their limit. You will be able to create a new CV again on " + resetDateStr + ".");
            }

            Date startOfToday = getStartOfDay(today);

            Calendar endCal = Calendar.getInstance();
            endCal.setTime(today);
            endCal.add(Calendar.DAY_OF_MONTH, 1);
            Date endDateForQuery = endCal.getTime();

            Integer totalMonthlyCount = cvCreationCountRepository.countCVCreatedInMonth(
                    candidate.getId(),
                    getStartOfMonth(today),
                    endDateForQuery
            );

            if (totalMonthlyCount == null) {
                totalMonthlyCount = 0;
            }

            if (totalMonthlyCount >= MAX_CVS_COUNT) {
                Date newLockDate = addDaysToDate(today, LOCK_PERIOD_DAYS);
                candidate.setPremiumUntil(newLockDate);
                candidateRepository.save(candidate);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String lockDateStr = sdf.format(newLockDate);

                throw new HttpBadRequest("Regular account has reached limit " + MAX_CVS_COUNT + " CV. You will be locked from creating a new CV until " + lockDateStr + ".");
            }

            CVCreationCount countEntity = cvCreationCountRepository.findByCandidateAndDate(candidate, startOfToday)
                    .orElse(CVCreationCount.builder()
                            .candidate(candidate)
                            .date(startOfToday)
                            .count(0)
                            .build());

            countEntity.setCount(countEntity.getCount() + 1);
            cvCreationCountRepository.save(countEntity);
        }

        CandidateCV newCV = CandidateCV.builder()
                .title(cvForm.getTitle())
                .template(cvForm.getTemplate())
                .candidate(candidate)
                .created_at(new Date())
                .updated_at(new Date())
                .build();

        mapPersonalInfo(newCV, cvForm, candidate);

        newCV = candidateCVRepository.save(newCV);

        updateAllCVDetails(newCV, cvForm);

        CandidateCV savedCV = candidateCVRepository.save(newCV);

        syncCVToArchive(savedCV);

        return savedCV;
    }

    @Override
    @Transactional
    public CandidateCV updateCV(Long cvId, FormCandidateCV cvForm, Long candidateId) {
        CandidateCV existingCV = candidateCVRepository.findByIdAndCandidate_Id(cvId, candidateId)
                .orElseThrow(() -> new HttpBadRequest("CV not found or does not belong to this candidate."));

        Candidate candidate = existingCV.getCandidate();

        existingCV.setTitle(cvForm.getTitle());
        existingCV.setTemplate(cvForm.getTemplate());
        existingCV.setUpdated_at(new Date());

        mapPersonalInfo(existingCV, cvForm, candidate);

        updateAllCVDetails(existingCV, cvForm);

        CandidateCV savedCV = candidateCVRepository.save(existingCV);

        syncCVToArchive(savedCV);

        return savedCV;
    }

    @Override
    @Transactional(readOnly = true)
    public CandidateCV getCVById(Long cvId, Long candidateId) {
        return candidateCVRepository.findByIdAndCandidate_Id(cvId, candidateId)
                .orElseThrow(() -> new HttpBadRequest("CV not found or does not belong to this candidate."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CandidateCV> getAllCVsByCandidate(Long candidateId) {
        return candidateCVRepository.findByCandidate_Id(candidateId);
    }

    @Override
    @Transactional
    public void deleteCV(Long cvId, Long candidateId) {
        CandidateCV cv = candidateCVRepository.findByIdAndCandidate_Id(cvId, candidateId)
                .orElseThrow(() -> new HttpBadRequest("CV not found or does not belong to this candidate."));

        candidateCVArchiveRepository.deleteByCandidateCVId(cvId);
        jobCandidateRepository.deleteByCandidateCVId(cvId);

        candidateCVRepository.delete(cv);
    }

    @Override
    @Transactional
    public void clearAllCandidateDetails(Long candidateId) {
        candidateCVArchiveRepository.deleteByCandidateId(candidateId);
        List<CandidateCV> cvList = candidateCVRepository.findByCandidate_Id(candidateId);
        candidateCVRepository.deleteAll(cvList);
    }

    private void updateAllCVDetails(CandidateCV candidateCV, FormCandidateCV cvForm) {
        final String DELIMITER = " | ";

        if (cvForm.getSkills() != null) {
            candidateCV.setSkillCandidateIds(null);

            candidateCV.setSkillCandidateNames(cvForm.getSkills().stream()
                    .map(s -> s.getContent() != null ? s.getContent() : "Unknown Skill")
                    .collect(Collectors.joining(DELIMITER)));
        } else {
            candidateCV.setSkillCandidateIds(null);
            candidateCV.setSkillCandidateNames(null);
        }

        if (cvForm.getEducations() != null) {

            candidateCV.setEducationCandidateNames(cvForm.getEducations().stream()
                    .map(e -> e.getNameEducation() != null ? e.getNameEducation() : "Unknown School")
                    .collect(Collectors.joining(DELIMITER)));

            candidateCV.setEducationCandidateGPA(cvForm.getEducations().stream()
                    .map(e -> e.getGpa() != null ? e.getGpa() : "-")
                    .collect(Collectors.joining(DELIMITER)));

            candidateCV.setEducationCandidateMajor(cvForm.getEducations().stream()
                    .map(e -> e.getMajor() != null ? e.getMajor() : "Unknown Major")
                    .collect(Collectors.joining(DELIMITER)));

            candidateCV.setEducationCandidateStartDates(cvForm.getEducations().stream()
                    .map(e -> e.getStartedAt() != null ? YEAR_FORMAT.format(e.getStartedAt()) : "?")
                    .collect(Collectors.joining(DELIMITER)));

            candidateCV.setEducationCandidateEndDates(cvForm.getEducations().stream()
                    .map(e -> e.getEndAt() != null ? YEAR_FORMAT.format(e.getEndAt()) : "Current")
                    .collect(Collectors.joining(DELIMITER)));

            candidateCV.setEducationCandidateInfo(cvForm.getEducations().stream()
                    .map(e -> (e.getInfo() != null ? e.getInfo() : "None") +
                            " (" + (e.getStartedAt() != null ? YEAR_FORMAT.format(e.getStartedAt()) : "?") +
                            " - " + (e.getEndAt() != null ? YEAR_FORMAT.format(e.getEndAt()) : "Current") + ")")
                    .collect(Collectors.joining(DELIMITER)));
        } else {
            candidateCV.setEducationCandidateNames(null);
            candidateCV.setEducationCandidateGPA(null);
            candidateCV.setEducationCandidateMajor(null);
            candidateCV.setEducationCandidateInfo(null);
            candidateCV.setEducationCandidateStartDates(null);
            candidateCV.setEducationCandidateEndDates(null);
        }

        if (cvForm.getExperiences() != null) {

            candidateCV.setExperienceCandidateNames(cvForm.getExperiences().stream()
                    .map(e -> (e.getPosition() != null ? e.getPosition() : "Unknown Position") +
                            " @ " + (e.getCompany() != null ? e.getCompany() : "Unknown Company"))
                    .collect(Collectors.joining(DELIMITER)));

            candidateCV.setExperienceCandidatePosition(cvForm.getExperiences().stream()
                    .map(e -> e.getPosition() != null ? e.getPosition() : "Unknown Position")
                    .collect(Collectors.joining(DELIMITER)));

            candidateCV.setExperienceCandidateCompany(cvForm.getExperiences().stream()
                    .map(e -> e.getCompany() != null ? e.getCompany() : "Unknown Company")
                    .collect(Collectors.joining(DELIMITER)));

            candidateCV.setExperienceCandidateStartDates(cvForm.getExperiences().stream()
                    .map(e -> e.getStarted_at() != null ? MONTH_YEAR_FORMAT.format(e.getStarted_at()) : "?")
                    .collect(Collectors.joining(DELIMITER)));

            candidateCV.setExperienceCandidateEndDates(cvForm.getExperiences().stream()
                    .map(e -> e.getEnd_at() != null ? MONTH_YEAR_FORMAT.format(e.getEnd_at()) : "Present")
                    .collect(Collectors.joining(DELIMITER)));

            candidateCV.setExperienceCandidateInfo(cvForm.getExperiences().stream()
                    .map(e -> {
                        String description = e.getDescription() != null && !e.getDescription().isEmpty()
                                ? String.join("\n- ", e.getDescription())
                                : "None";

                        description = description.equals("None") ? description : "- " + description;

                        return description +
                                " (" + (e.getStarted_at() != null ? MONTH_YEAR_FORMAT.format(e.getStarted_at()) : "?") +
                                " - " + (e.getEnd_at() != null ? MONTH_YEAR_FORMAT.format(e.getEnd_at()) : "Present") + ")";
                    })
                    .collect(Collectors.joining(DELIMITER)));
        } else {
            candidateCV.setExperienceCandidateNames(null);
            candidateCV.setExperienceCandidatePosition(null);
            candidateCV.setExperienceCandidateCompany(null);
            candidateCV.setExperienceCandidateInfo(null);
            candidateCV.setExperienceCandidateStartDates(null);
            candidateCV.setExperienceCandidateEndDates(null);
        }

        if (cvForm.getCertificates() != null) {

            candidateCV.setCertificateCandidateNames(cvForm.getCertificates().stream()
                    .map(c -> c.getName() != null ? c.getName() : "Untitled Certificate")
                    .collect(Collectors.joining(DELIMITER)));

            candidateCV.setCertificateCandidateOrganization(cvForm.getCertificates().stream()
                    .map(c -> c.getOrganization() != null ? c.getOrganization() : "Unknown Organization")
                    .collect(Collectors.joining(DELIMITER)));

            candidateCV.setCertificateCandidateStartDates(cvForm.getCertificates().stream()
                    .map(c -> c.getStarted_at() != null ? YEAR_FORMAT.format(c.getStarted_at()) : "?")
                    .collect(Collectors.joining(DELIMITER)));

            candidateCV.setCertificateCandidateEndDates(cvForm.getCertificates().stream()
                    .map(c -> c.getEnd_at() != null ? YEAR_FORMAT.format(c.getEnd_at()) : "N/A")
                    .collect(Collectors.joining(DELIMITER)));

            candidateCV.setCertificateCandidateInfo(cvForm.getCertificates().stream()
                    .map(c -> (c.getYear() != null ? "Năm cấp: " + c.getYear() : "N/A") +
                            (c.getInfo() != null && !c.getInfo().isEmpty() ? " - " + c.getInfo() : ""))
                    .collect(Collectors.joining(DELIMITER)));
        } else {
            candidateCV.setCertificateCandidateNames(null);
            candidateCV.setCertificateCandidateOrganization(null);
            candidateCV.setCertificateCandidateInfo(null);
            candidateCV.setCertificateCandidateStartDates(null);
            candidateCV.setCertificateCandidateEndDates(null);
        }

        if (cvForm.getProjects() != null) {
            candidateCV.setProjectCandidateNames(cvForm.getProjects().stream()
                    .map(p -> p.getName() != null ? p.getName() : "Untitled Project")
                    .collect(Collectors.joining(DELIMITER)));

            candidateCV.setProjectCandidateLink(cvForm.getProjects().stream()
                    .map(p -> p.getLink() != null ? p.getLink() : "None")
                    .collect(Collectors.joining(DELIMITER)));

            candidateCV.setProjectCandidateStartDates(cvForm.getProjects().stream()
                    .map(p -> p.getStarted_at() != null ? YEAR_FORMAT.format(p.getStarted_at()) : "?")
                    .collect(Collectors.joining(DELIMITER)));

            candidateCV.setProjectCandidateEndDates(cvForm.getProjects().stream()
                    .map(p -> p.getEnd_at() != null ? YEAR_FORMAT.format(p.getEnd_at()) : "Current")
                    .collect(Collectors.joining(DELIMITER)));

            candidateCV.setProjectCandidateInfo(cvForm.getProjects().stream()
                    .map(p -> (p.getInfo() != null ? p.getInfo() : "None") +
                            " (" + (p.getStarted_at() != null ? YEAR_FORMAT.format(p.getStarted_at()) : "?") +
                            " - " + (p.getEnd_at() != null ? YEAR_FORMAT.format(p.getEnd_at()) : "Current") + ")")
                    .collect(Collectors.joining(DELIMITER)));
        } else {
            candidateCV.setProjectCandidateNames(null);
            candidateCV.setProjectCandidateLink(null);
            candidateCV.setProjectCandidateInfo(null);
            candidateCV.setProjectCandidateStartDates(null);
            candidateCV.setProjectCandidateEndDates(null);
        }
    }

    private void syncCVToArchive(CandidateCV cvEntity) {
        Optional<CandidateCVArchive> existingArchive = candidateCVArchiveRepository
                .findByCandidateCVIdAndCandidateId(cvEntity.getId(), cvEntity.getCandidate().getId());

        CandidateCVArchive archive = existingArchive.orElse(CandidateCVArchive.builder()
                .candidateId(cvEntity.getCandidate().getId())
                .candidateCVId(cvEntity.getId())
                .createdAt(new Date())
                .build());

        archive.setCandidateName(cvEntity.getName());
        archive.setDob(cvEntity.getDob());
        archive.setEmail(cvEntity.getEmail());
        archive.setPhone(cvEntity.getPhone());
        archive.setAddress(cvEntity.getAddress());
        archive.setLink(cvEntity.getLink());
        archive.setDevelopment(cvEntity.getDevelopment());
        archive.setCandidateTitle(cvEntity.getCandidateTitle());
        archive.setHobbies(cvEntity.getHobbies());

        archive.setSkillCandidateIds(cvEntity.getSkillCandidateIds());
        archive.setSkillCandidateNames(cvEntity.getSkillCandidateNames());

        archive.setEducationCandidateIds(cvEntity.getEducationCandidateIds());
        archive.setEducationCandidateNames(cvEntity.getEducationCandidateNames());
        archive.setEducationCandidateGPA(cvEntity.getEducationCandidateGPA());
        archive.setEducationCandidateMajor(cvEntity.getEducationCandidateMajor());
        archive.setEducationCandidateStartDates(cvEntity.getEducationCandidateStartDates());
        archive.setEducationCandidateEndDates(cvEntity.getEducationCandidateEndDates());

        archive.setExperienceCandidateIds(cvEntity.getExperienceCandidateIds());
        archive.setExperienceCandidateNames(cvEntity.getExperienceCandidateNames());
        archive.setExperienceCandidateStartDates(cvEntity.getExperienceCandidateStartDates());
        archive.setExperienceCandidateEndDates(cvEntity.getExperienceCandidateEndDates());

        archive.setProjectCandidateIds(cvEntity.getProjectCandidateIds());
        archive.setProjectCandidateNames(cvEntity.getProjectCandidateNames());
        archive.setProjectCandidateStartDates(cvEntity.getProjectCandidateStartDates());
        archive.setProjectCandidateEndDates(cvEntity.getProjectCandidateEndDates());

        archive.setCertificateCandidateIds(cvEntity.getCertificateCandidateIds());
        archive.setCertificateCandidateNames(cvEntity.getCertificateCandidateNames());
        archive.setCertificateCandidateStartDates(cvEntity.getCertificateCandidateStartDates());
        archive.setCertificateCandidateEndDates(cvEntity.getCertificateCandidateEndDates());

        archive.setTitle(cvEntity.getTitle());
        archive.setUpdatedAt(new Date());

        candidateCVArchiveRepository.save(archive);
    }

    @Override
    @Transactional
    public void deleteCVArchive(Long archiveId) {
        candidateCVArchiveRepository.findById(archiveId)
                .orElseThrow(() -> new HttpBadRequest("CV Archive not found with ID: " + archiveId));
        candidateCVArchiveRepository.deleteById(archiveId);
    }

    @Override
    @Transactional
    public CandidateCVArchive updateCVArchive(Long archiveId, FormCandidateCVArchive updateForm) {
        CandidateCVArchive existingArchive = candidateCVArchiveRepository.findById(archiveId)
                .orElseThrow(() -> new HttpBadRequest("CV Archive not found with ID: " + archiveId));

        if (updateForm.getTitle() != null) {
            existingArchive.setTitle(updateForm.getTitle());
        }

        if (updateForm.getSkillCandidateIds() != null) {
            existingArchive.setSkillCandidateIds(updateForm.getSkillCandidateIds());
        }

        if (updateForm.getEducationCandidateIds() != null) {
            existingArchive.setEducationCandidateIds(updateForm.getEducationCandidateIds());
        }

        existingArchive.setUpdatedAt(new Date());

        return candidateCVArchiveRepository.save(existingArchive);
    }

    public CandidateCVArchive mapArchiveToResponse(CandidateCVArchive archive) {
        return archive;
    }

    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }

        String cleanText = text.replace('#', ' ');

        return cleanText.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    @Override
    public byte[] generatePdfFromCV(Long cvId, Long candidateId) {
        CandidateCV cvEntity = getCVById(cvId, candidateId);

        String htmlContent = generateHtmlContent(cvEntity);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {

            PdfRendererBuilder builder = new PdfRendererBuilder();

            File fontFile = new File("src/main/resources/fonts/arial.ttf");

            String fontName = "ArialUnicode";

            if (fontFile.exists()) {

                builder.useFont(fontFile, fontName);


                htmlContent = htmlContent.replace("font-family: Arial, sans-serif;", "font-family: " + fontName + ", sans-serif;");
            }

            builder.withHtmlContent(htmlContent, "file:///base/");

            builder.toStream(os);

            builder.run();

            return os.toByteArray();

        } catch (IOException e) {

            throw new RuntimeException("Error during HTML to PDF compilation: " + e.getMessage(), e);
        }
    }

    /**
     * THAY THẾ PHƯƠNG THỨC TẠO HTML TĨNH BẰNG LOGIC RENDER TEMPLATE ĐỘNG (Thymeleaf)
     */
    public String generateHtmlContent(CandidateCV cvEntity) {
        String templateNumber = cvEntity.getTemplate();
        if (templateNumber == null || templateNumber.trim().isEmpty()) {
            templateNumber = "default";
        } else {
            templateNumber = templateNumber.trim();
        }

        CandidateCVResponse cvData = mapToResponse(cvEntity);

        Context context = new Context(Locale.ENGLISH);
        context.setVariable("cvData", cvData);

        String templateFile = "cv-" + templateNumber;

        try {
            return templateEngine.process(templateFile, context);
        } catch (TemplateInputException e) {
            String errorMessage = String.format("CV Template '%s' not found. Check if file 'templates/cv/%s.html' exists.",
                    templateNumber, templateFile);
            throw new RuntimeException(errorMessage, e);
        } catch (Exception e) {
            String errorMessage = String.format("Failed to process CV Template '%s'. Check template syntax or data logic.",
                    templateNumber);
            throw new RuntimeException(errorMessage, e);
        }
    }
    @Override
    public CandidateCV getPublicCVById(Long cvId) {
        return candidateCVRepository.findById(cvId)
                .filter(CandidateCV::getIsPublic)
                .orElseThrow(() -> new HttpBadRequest("Public CV not found with ID: " + cvId));

    }
    @Override
    public List<CandidateCV> getCVsByPublicStatus(boolean isPublic) {
        // Gọi Repository với cú pháp đúng của JPA
        return candidateCVRepository.findByIsPublic(isPublic);
    }
    @Override
    @Transactional(readOnly = true)
    public List<CandidateCV> getAllPublicCVsByCandidateId(Long candidateId) {
        // Gọi phương thức Repository mới
        return candidateCVRepository.findByCandidate_IdAndIsPublic(candidateId, true);
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
}