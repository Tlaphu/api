package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.*;
import com.ra.base_spring_boot.dto.resp.CandidateCVResponse;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.model.*;
import com.ra.base_spring_boot.repository.*;
import com.ra.base_spring_boot.services.ICandidateCVService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateCVServiceImpl implements ICandidateCVService {

    private final ICandidateRepository candidateRepository;
    private final ICandidateCVRepository candidateCVRepository;

    private final ICVCreationCountRepository cvCreationCountRepository;
    private final ICandidateCVArchiveRepository candidateCVArchiveRepository;
    private final IJobCandidateRepository jobCandidateRepository;

    private static final SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy");
    private static final SimpleDateFormat MONTH_YEAR_FORMAT = new SimpleDateFormat("MM/yyyy");

    public CandidateCVResponse mapToResponse(CandidateCV candidateCV) {
        Candidate candidate = candidateCV.getCandidate();
        final String DELIMITER = " | ";

        // --- PROJECTS ---
        List<String> projectNames = splitStringToList(candidateCV.getProjectCandidateNames(), DELIMITER);
        List<String> projectLinks = splitStringToList(candidateCV.getProjectCandidateLink(), DELIMITER);
        List<String> projectInfos = splitStringToList(candidateCV.getProjectCandidateInfo(), DELIMITER);

        // --- SKILLS ---
        List<String> skillNames = splitStringToList(candidateCV.getSkillCandidateNames(), DELIMITER);

        // --- EDUCATIONS ---
        List<String> educationNames = splitStringToList(candidateCV.getEducationCandidateNames(), DELIMITER);
        List<String> educationMajors = splitStringToList(candidateCV.getEducationCandidateMajor(), DELIMITER);
        List<String> educationGPAs = splitStringToList(candidateCV.getEducationCandidateGPA(), DELIMITER);
        List<String> educationInfos = splitStringToList(candidateCV.getEductaionCandidateInfo(), DELIMITER);

        // --- EXPERIENCES ---
        List<String> experienceNames = splitStringToList(candidateCV.getExperienceCandidateNames(), DELIMITER); // Role @ Company
        List<String> experiencePositions = splitStringToList(candidateCV.getExperienceCandidatePosition(), DELIMITER); // Role/Position
        List<String> experienceCompanies = splitStringToList(candidateCV.getExperienceCandidateCompany(), DELIMITER); // Company
        List<String> experienceInfos = splitStringToList(candidateCV.getExperienceCandidateInfo(), DELIMITER); // Description + Time

        // --- CERTIFICATES ---
        List<String> certificateNames = splitStringToList(candidateCV.getCertificateCandidateNames(), DELIMITER);
        List<String> certificateOrganizations = splitStringToList(candidateCV.getCertificateCandidateOrganization(), DELIMITER);
        List<String> certificateInfos = splitStringToList(candidateCV.getCertificateCandidateInfo(), DELIMITER); // Year + Info

        return CandidateCVResponse.builder()
                .id(candidateCV.getId())
                .name(candidateCV.getName())
                .dob(candidateCV.getDob())
                .address(candidateCV.getAddress())
                .title(candidateCV.getTitle())
                .template(candidateCV.getTemplate())

                .gender(candidate.getGender())
                .link(candidate.getLink())
                .description(candidate.getDescription())
                .development(candidate.getDevelopment())
                .candidateTitle(candidateCV.getCandidateTitle())

                // --- CÁC TRƯỜNG CÁ NHÂN KHÁC ---
                .email(candidateCV.getEmail())
                .phone(candidateCV.getPhone())
                // Giả định các trường này có trong entity CandidateCV
                .avatar(candidateCV.getAvatar())
                .hobbies(candidateCV.getHobbies())

                // --- GÁN DỮ LIỆU CHI TIẾT ĐÃ TÁCH ---
                .projects(projectNames)
                .projectLinks(projectLinks)
                .projectInfos(projectInfos)

                .skills(skillNames)

                .educations(educationNames)
                .educationMajors(educationMajors)
                .educationGPAs(educationGPAs)
                .educationInfos(educationInfos)

                .experiences(experienceNames) // Role @ Company
                .experiencePositions(experiencePositions)
                .experienceCompanies(experienceCompanies)
                .experienceInfos(experienceInfos)

                .certificates(certificateNames)
                .certificateOrganizations(certificateOrganizations)
                .certificateInfos(certificateInfos)

                .build();
    }

    private List<String> splitStringToList(String data, String delimiter) {
        if (data == null || data.trim().isEmpty()) {
            return List.of();
        }

        // SỬA LỖI: Escape ký tự gạch đứng (|) vì String.split() sử dụng Regex.
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
        cvEntity.setDob(cvForm.getDob() != null ? cvForm.getDob() : candidate.getDob());
        cvEntity.setEmail(cvForm.getEmail() != null ? cvForm.getEmail() : candidate.getEmail());
        cvEntity.setPhone(cvForm.getPhone() != null ? cvForm.getPhone() : candidate.getPhone());
        cvEntity.setAddress(cvForm.getAddress() != null ? cvForm.getAddress() : candidate.getAddress());
        cvEntity.setLink(cvForm.getLink() != null ? cvForm.getLink() : candidate.getLink());
        cvEntity.setDescription(cvForm.getDescription() != null ? cvForm.getDescription() : candidate.getDescription());
        cvEntity.setDevelopment(cvForm.getDevelopment() != null ? cvForm.getDevelopment() : candidate.getDevelopment());
        cvEntity.setCandidateTitle(cvForm.getCandidateTitle() != null ? cvForm.getCandidateTitle() : candidate.getTitle());
        cvEntity.setHobbies(cvForm.getHobbies()!= null ? cvForm.getHobbies() : cvEntity.getHobbies());
        cvEntity.setAvatar(cvForm.getAvatar() != null ? cvForm.getAvatar() : cvEntity.getAvatar());
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


        // --- 1. SKILLS ---
        if (cvForm.getSkills() != null) {
            candidateCV.setSkillCandidateIds(null); // Giả định không dùng ID từ Form mới

            // SỬ DỤNG getContent()
            candidateCV.setSkillCandidateNames(cvForm.getSkills().stream()
                    .map(s -> s.getContent() != null ? s.getContent() : "Unknown Skill")
                    .collect(Collectors.joining(" | ")));
        } else {
            candidateCV.setSkillCandidateIds(null);
            candidateCV.setSkillCandidateNames(null);
        }


        // --- 2. EDUCATION ---
        if (cvForm.getEducations() != null) {
            // SỬ DỤNG getNameEducation()
            candidateCV.setEducationCandidateNames(cvForm.getEducations().stream()
                    .map(e -> e.getNameEducation() != null ? e.getNameEducation() : "Unknown School")
                    .collect(Collectors.joining(" | ")));

            // GPA giữ nguyên
            candidateCV.setEducationCandidateGPA(cvForm.getEducations().stream()
                    .map(e -> e.getGpa() != null ? e.getGpa() : "-")
                    .collect(Collectors.joining(" | ")));

            // Major giữ nguyên
            candidateCV.setEducationCandidateMajor(cvForm.getEducations().stream()
                    .map(e -> e.getMajor() != null ? e.getMajor() : "Unknown Major")
                    .collect(Collectors.joining(" | ")));

            // SỬ DỤNG getInfo()
            candidateCV.setEductaionCandidateInfo(cvForm.getEducations().stream()
                    .map(e -> (e.getInfo() != null ? e.getInfo() : "None") +
                            " (" + (e.getStartedAt() != null ? YEAR_FORMAT.format(e.getStartedAt()) : "?") +
                            " - " + (e.getEndAt() != null ? YEAR_FORMAT.format(e.getEndAt()) : "Current") + ")")
                    .collect(Collectors.joining(" | ")));
        } else {
            candidateCV.setEducationCandidateNames(null);
            candidateCV.setEducationCandidateGPA(null);
            candidateCV.setEducationCandidateMajor(null);
            candidateCV.setEductaionCandidateInfo(null);
        }

        // --- 3. EXPERIENCE ---
        if (cvForm.getExperiences() != null) {
            // Experience Combined Name (Role @ Company)


            // Company giữ nguyên
            candidateCV.setExperienceCandidateCompany(cvForm.getExperiences().stream()
                    .map(e -> e.getCompany() != null ? e.getCompany() : "Unknown Company")
                    .collect(Collectors.joining(" | ")));

            // Experience Info (Nối List<String> description thành chuỗi lớn + Thời gian)
            candidateCV.setExperienceCandidateInfo(cvForm.getExperiences().stream()
                    .map(e -> {
                        // Nối List<String> description thành một chuỗi với dấu gạch ngang đầu dòng
                        String description = e.getDescription() != null && !e.getDescription().isEmpty()
                                ? String.join("\n- ", e.getDescription())
                                : "None";
                        // Thêm dấu gạch ngang đầu tiên nếu có mô tả chi tiết
                        description = description.equals("None") ? description : "- " + description;

                        return description +
                                " (" + (e.getStarted_at() != null ? MONTH_YEAR_FORMAT.format(e.getStarted_at()) : "?") +
                                " - " + (e.getEnd_at() != null ? MONTH_YEAR_FORMAT.format(e.getEnd_at()) : "Present") + ")";
                    })
                    .collect(Collectors.joining(" | ")));
        } else {
            candidateCV.setExperienceCandidateNames(null);
            candidateCV.setExperienceCandidatePosition(null);
            candidateCV.setExperienceCandidateCompany(null);
            candidateCV.setExperienceCandidateInfo(null);
        }

        // --- 4. CERTIFICATE ---
        if (cvForm.getCertificates() != null) {
            candidateCV.setCertificateCandidateNames(cvForm.getCertificates().stream()
                    .map(c -> c.getName() != null ? c.getName() : "Untitled Certificate")
                    .collect(Collectors.joining(" | ")));

            candidateCV.setCertificateCandidateOrganization(cvForm.getCertificates().stream()
                    .map(c -> c.getOrganization() != null ? c.getOrganization() : "Unknown Organization")
                    .collect(Collectors.joining(" | ")));

            // Kết hợp Year và Info
            candidateCV.setCertificateCandidateInfo(cvForm.getCertificates().stream()
                    .map(c -> (c.getYear() != null ? "Năm cấp: " + c.getYear() : "N/A") +
                            (c.getInfo() != null && !c.getInfo().isEmpty() ? " - " + c.getInfo() : ""))
                    .collect(Collectors.joining(" | ")));
        } else {
            candidateCV.setCertificateCandidateNames(null);
            candidateCV.setCertificateCandidateOrganization(null);
            candidateCV.setCertificateCandidateInfo(null);
        }

        // --- 5. PROJECT ---
        if (cvForm.getProjects() != null) {
            candidateCV.setProjectCandidateNames(cvForm.getProjects().stream()
                    .map(p -> p.getName() != null ? p.getName() : "Untitled Project")
                    .collect(Collectors.joining(" | ")));

            candidateCV.setProjectCandidateLink(cvForm.getProjects().stream()
                    .map(p -> p.getLink() != null ? p.getLink() : "None")
                    .collect(Collectors.joining(" | ")));

            // Info (Mô tả) + Thời gian hoạt động
            candidateCV.setProjectCandidateInfo(cvForm.getProjects().stream()
                    .map(p -> (p.getInfo() != null ? p.getInfo() : "None") +
                            " (" + (p.getStarted_at() != null ? YEAR_FORMAT.format(p.getStarted_at()) : "?") +
                            " - " + (p.getEnd_at() != null ? YEAR_FORMAT.format(p.getEnd_at()) : "Current") + ")")
                    .collect(Collectors.joining(" | ")));
        } else {
            candidateCV.setProjectCandidateNames(null);
            candidateCV.setProjectCandidateLink(null);
            candidateCV.setProjectCandidateInfo(null);
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

        // Thông tin cá nhân
        archive.setCandidateName(cvEntity.getName());
        archive.setDob(cvEntity.getDob());
        archive.setEmail(cvEntity.getEmail());
        archive.setPhone(cvEntity.getPhone());
        archive.setAddress(cvEntity.getAddress());
        archive.setLink(cvEntity.getLink());
        archive.setDevelopment(cvEntity.getDevelopment());
        archive.setCandidateTitle(cvEntity.getCandidateTitle());

        // Kỹ năng tổng hợp
        archive.setSkillCandidateIds(cvEntity.getSkillCandidateIds());
        archive.setSkillCandidateNames(cvEntity.getSkillCandidateNames());

        // Học vấn tổng hợp
        archive.setEducationCandidateIds(cvEntity.getEducationCandidateIds());
        archive.setEducationCandidateNames(cvEntity.getEducationCandidateNames());
        archive.setEducationCandidateGPA(cvEntity.getEducationCandidateGPA());
        archive.setEducationCandidateMajor(cvEntity.getEducationCandidateMajor());

        // Kinh nghiệm tổng hợp
        archive.setExperienceCandidateIds(cvEntity.getExperienceCandidateIds());
        archive.setExperienceCandidateNames(cvEntity.getExperienceCandidateNames());

        // Dự án tổng hợp
        archive.setProjectCandidateIds(cvEntity.getProjectCandidateIds());
        archive.setProjectCandidateNames(cvEntity.getProjectCandidateNames());

        // Chứng chỉ tổng hợp
        archive.setCertificateCandidateIds(cvEntity.getCertificateCandidateIds());
        archive.setCertificateCandidateNames(cvEntity.getCertificateCandidateNames());

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


    // ⭐ Phương thức generatePdfFromCV (Đã tích hợp OpenHTMLToPDF)
    @Override
    public byte[] generatePdfFromCV(Long cvId, Long candidateId) {
        CandidateCV cvEntity = getCVById(cvId, candidateId);

        // Bước 1: Tạo nội dung HTML (thay vì LaTeX)
        String htmlContent = generateHtmlContent(cvEntity);

        // Bước 2: Biên dịch HTML sang PDF sử dụng OpenHTMLToPDF
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {

            PdfRendererBuilder builder = new PdfRendererBuilder();


            builder.withHtmlContent(htmlContent, "file:///base/");


            builder.toStream(os);


            builder.run();

            return os.toByteArray();

        } catch (IOException e) {

            throw new RuntimeException("Error during HTML to PDF compilation: " + e.getMessage(), e);
        }
    }



    public String generateHtmlContent(CandidateCV cvEntity) {

        String candidateName = cvEntity.getName() != null ? cvEntity.getName() : "Unknown Candidate";
        String candidateAddress = cvEntity.getAddress() != null ? cvEntity.getAddress() : "";
        String candidateEmail = cvEntity.getEmail() != null ? cvEntity.getEmail() : "";
        String candidatePhone = cvEntity.getPhone() != null ? cvEntity.getPhone() : "";
        String candidateDevelopment = cvEntity.getDevelopment() != null ? cvEntity.getDevelopment() : "N/A";
        String candidateTitle = cvEntity.getCandidateTitle() != null ? cvEntity.getCandidateTitle() : "";

        // Lấy các List dữ liệu
        List<String> skills = splitStringToList(cvEntity.getSkillCandidateNames(), " | ");
        List<String> educationNames = splitStringToList(cvEntity.getEducationCandidateNames(), " | ");
        List<String> educationMajors = splitStringToList(cvEntity.getEducationCandidateMajor(), " | ");
        List<String> educationInfos = splitStringToList(cvEntity.getEductaionCandidateInfo(), " | ");

        // Cần các list khác cho Experience, Project, Certificate
        List<String> experienceNames = splitStringToList(cvEntity.getExperienceCandidateNames(), " | ");
        List<String> experienceInfos = splitStringToList(cvEntity.getExperienceCandidateInfo(), " | ");
        List<String> projectNames = splitStringToList(cvEntity.getProjectCandidateNames(), " | ");
        List<String> projectLinks = splitStringToList(cvEntity.getProjectCandidateLink(), " | ");
        List<String> projectInfos = splitStringToList(cvEntity.getProjectCandidateInfo(), " | ");
        List<String> certificateNames = splitStringToList(cvEntity.getCertificateCandidateNames(), " | ");
        List<String> certificateOrgs = splitStringToList(cvEntity.getCertificateCandidateOrganization(), " | ");


        // --- Bắt đầu xây dựng HTML ---
        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>")
                .append("body{font-family: Arial, sans-serif; margin: 0; padding: 20px;} h1{text-align: center; margin-bottom: 5px;}")
                .append(".contact{text-align: center; font-size: 0.9em; color: #555;} .title{text-align: center; font-size: 1.1em; font-weight: bold; color: #333;}")
                .append(".section{margin-top: 15px; border-bottom: 2px solid #ccc; padding-bottom: 3px; font-size: 1.2em; color: #000;}")
                .append(".item-title{font-weight: bold; margin-bottom: 2px;} .item-detail{margin-left: 20px; font-size: 0.95em;}")
                .append("</style></head><body>");

        // --- 1. Header & Personal Info ---
        html.append("<h1>").append(candidateName).append("</h1>")
                .append("<p class='contact'>")
                .append(candidatePhone).append(" | ").append(candidateEmail).append(" | ").append(candidateAddress)
                .append("</p>")
                .append("<div class='title'>").append(candidateTitle).append("</div>");

        // --- 2. Development Summary ---
        html.append("<h2 class='section'>Summary / Development Goal</h2>")
                .append("<p>").append(candidateDevelopment).append("</p>");

        // --- 3. Skills ---
        html.append("<h2 class='section'>Skills</h2>")
                .append("<ul>");
        skills.forEach(skill -> html.append("<li>").append(skill).append("</li>"));
        html.append("</ul>");

        // --- 4. Experience ---
        html.append("<h2 class='section'>Experience</h2>");
        for (int i = 0; i < experienceNames.size(); i++) {
            html.append("<div class='item-title'>").append(experienceNames.get(i)).append("</div>")
                    .append("<div class='item-detail'>").append(experienceInfos.get(i)).append("</div>");
        }

        // --- 5. Education ---
        html.append("<h2 class='section'>Education</h2>");
        for (int i = 0; i < educationNames.size(); i++) {
            String major = i < educationMajors.size() ? educationMajors.get(i) : "";
            String info = i < educationInfos.size() ? educationInfos.get(i) : "";

            html.append("<div class='item-title'>").append(educationNames.get(i)).append("</div>")
                    .append("<div class='item-detail'>").append(major).append(" | ").append(info).append("</div>");
        }

        // --- 6. Projects ---
        html.append("<h2 class='section'>Projects</h2>");
        for (int i = 0; i < projectNames.size(); i++) {
            String link = i < projectLinks.size() ? projectLinks.get(i) : "N/A";
            String info = i < projectInfos.size() ? projectInfos.get(i) : "N/A";

            html.append("<div class='item-title'>").append(projectNames.get(i)).append("</div>")
                    .append("<div class='item-detail'>").append(info).append(" | Link: <a href='").append(link).append("'>").append(link).append("</a></div>");
        }

        // --- 7. Certificates ---
        html.append("<h2 class='section'>Certificates</h2>");
        for (int i = 0; i < certificateNames.size(); i++) {
            String org = i < certificateOrgs.size() ? certificateOrgs.get(i) : "";
            html.append("<div class='item-title'>").append(certificateNames.get(i)).append("</div>")
                    .append("<div class='item-detail'>Issued by: ").append(org).append("</div>");
        }


        html.append("</body></html>");
        return html.toString();
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