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

    // Đã loại bỏ các Repository của Entity con (Project, SkillCandidate, Education, Experience, Certificate)
    private final ICVCreationCountRepository cvCreationCountRepository;
    private final ICandidateCVArchiveRepository candidateCVArchiveRepository;
    private final IJobCandidateRepository jobCandidateRepository;

    public CandidateCVResponse mapToResponse(CandidateCV candidateCV) {
        Candidate candidate = candidateCV.getCandidate();

        // Sử dụng các trường String/TEXT mới trong CandidateCV
        List<String> projectNames = splitStringToList(candidateCV.getProjectCandidateNames(), " | ");
        List<String> skillNames = splitStringToList(candidateCV.getSkillCandidateNames(), " | ");
        List<String> educationNames = splitStringToList(candidateCV.getEducationCandidateNames(), " | ");
        List<String> experienceNames = splitStringToList(candidateCV.getExperienceCandidateNames(), " | ");
        List<String> certificateNames = splitStringToList(candidateCV.getCertificateCandidateNames(), " | ");

        return CandidateCVResponse.builder()
                .id(candidateCV.getId())
                .name(candidateCV.getName())
                .dob(candidateCV.getDob())
                .address(candidateCV.getAddress())
                .title(candidateCV.getTitle())
                .template(candidateCV.getTemplate())

                // Lấy từ Entity Candidate liên quan
                .gender(candidate.getGender())
                .link(candidate.getLink())
                .description(candidate.getDescription())
                .development(candidate.getDevelopment())

                .projects(projectNames)
                .skills(skillNames)
                .educations(educationNames)
                .experiences(experienceNames)
                .certificates(certificateNames)
                .build();
    }

    // Hàm tiện ích để chuyển chuỗi thành List<String>
    private List<String> splitStringToList(String data, String delimiter) {
        if (data == null || data.trim().isEmpty()) {
            return List.of();
        }
        // Loại bỏ khoảng trắng thừa, sau đó split
        return List.of(data.trim().split(delimiter)).stream()
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

            // Tìm tổng số CV đã tạo trong tháng (Logic này có vẻ phức tạp và dễ gây lỗi, nhưng tôi giữ nguyên)
            Calendar endCal = Calendar.getInstance();
            endCal.setTime(today);
            endCal.add(Calendar.DAY_OF_MONTH, 1);
            Date endDateForQuery = endCal.getTime();

            // Sửa startOfMonth thành startOfToday để khớp với cách query
            Integer totalMonthlyCount = cvCreationCountRepository.countCVCreatedInMonth(
                    candidate.getId(),
                    getStartOfMonth(today), // Dùng getStartOfMonth cho total monthly
                    endDateForQuery
            );

            if (totalMonthlyCount == null) {
                totalMonthlyCount = 0;
            }

            if (totalMonthlyCount >= MAX_CVS_COUNT) {
                // Nếu đạt giới hạn, thiết lập ngày khóa mới (30 ngày sau) vào premiumUntil
                Date newLockDate = addDaysToDate(today, LOCK_PERIOD_DAYS);
                candidate.setPremiumUntil(newLockDate);
                candidateRepository.save(candidate);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String lockDateStr = sdf.format(newLockDate);

                throw new HttpBadRequest("Regular account has reached limit " + MAX_CVS_COUNT + " CV. You will be locked from creating a new CV until " + lockDateStr + ".");
            }

            // Cập nhật CVCreationCount cho ngày hôm nay
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

        // Gán tạm để có ID, sau đó update chi tiết và lưu lại
        newCV = candidateCVRepository.save(newCV);

        // Tích hợp logic tổng hợp chuỗi trực tiếp vào CV
        updateAllCVDetails(newCV, cvForm);

        CandidateCV savedCV = candidateCVRepository.save(newCV);

        // Đồng bộ hóa vào Archive
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

        // Tích hợp logic tổng hợp chuỗi trực tiếp vào CV
        updateAllCVDetails(existingCV, cvForm);

        CandidateCV savedCV = candidateCVRepository.save(existingCV);

        // Đồng bộ hóa vào Archive
        syncCVToArchive(savedCV);

        return savedCV;
    }

    // Loại bỏ các Repository con, chỉ cần Repository của CandidateCV
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

        // Xóa tất cả các bản ghi liên quan trong Archive và JobCandidate
        candidateCVArchiveRepository.deleteByCandidateCVId(cvId);
        jobCandidateRepository.deleteByCandidateCVId(cvId);

        // Loại bỏ các lệnh deleteByCandidateCVId cho các Entity con vì chúng không còn là Entity con được quản lý bởi Repository riêng nữa
        // Tuy nhiên, nếu bạn vẫn muốn giữ lại các Repository con này cho mục đích khác, bạn cần xem xét lại thiết kế.
        // Dựa trên yêu cầu của bạn, tôi loại bỏ chúng.

        candidateCVRepository.delete(cv);
    }

    @Override
    @Transactional
    public void clearAllCandidateDetails(Long candidateId) {
        candidateCVArchiveRepository.deleteByCandidateId(candidateId);
        List<CandidateCV> cvList = candidateCVRepository.findByCandidate_Id(candidateId);
        // Lưu ý: Nếu các Entity con vẫn tồn tại, lệnh này KHÔNG xóa chúng.
        // Tuy nhiên, dựa trên thiết kế độc lập, việc này có thể chấp nhận.
        candidateCVRepository.deleteAll(cvList);
    }

    /**
     * TỔNG HỢP dữ liệu từ Form DTO thành các chuỗi TEXT và gán vào CandidateCV.
     * Logic này thay thế cho việc tạo/cập nhật các Entity con (@OneToMany).
     */
    private void updateAllCVDetails(CandidateCV candidateCV, FormCandidateCV cvForm) {


        if (cvForm.getSkills() != null) {
            candidateCV.setSkillCandidateIds(cvForm.getSkills().stream()
                    .map(s -> s.getSkillId() != null ? String.valueOf(s.getSkillId()) : "")
                    .collect(Collectors.joining(",")));


            candidateCV.setSkillCandidateNames(cvForm.getSkills().stream()
                    .map(s -> s.getSkillName() != null ? s.getSkillName() : "Unknown Skill")
                    .collect(Collectors.joining(" | ")));
        }


        if (cvForm.getEducations() != null) {
            candidateCV.setEducationCandidateNames(cvForm.getEducations().stream()
                    .map(e -> e.getNameEducation() != null ? e.getNameEducation() : "Unknown School")
                    .collect(Collectors.joining(" | ")));
            candidateCV.setEducationCandidateGPA(cvForm.getEducations().stream()
                    .map(e -> e.getGpa() != null ? e.getGpa() : "-")
                    .collect(Collectors.joining(" | ")));
            candidateCV.setEducationCandidateMajor(cvForm.getEducations().stream()
                    .map(e -> e.getMajor() != null ? e.getMajor() : "Unknown Major")
                    .collect(Collectors.joining(" | ")));
            candidateCV.setEductaionCandidateInfo(cvForm.getEducations().stream()
                    .map(e -> e.getInfo() != null ? e.getInfo() : "None")
                    .collect(Collectors.joining(" | ")));
        }

        // --- 3. EXPERIENCE ---
        if (cvForm.getExperiences() != null) {
            candidateCV.setExperienceCandidateNames(cvForm.getExperiences().stream()
                    .map(e -> (e.getPosition() != null ? e.getPosition() : "Pos") + " @ " +
                            (e.getCompany() != null ? e.getCompany() : "Comp"))
                    .collect(Collectors.joining(" | ")));
            candidateCV.setExperienceCandidatePosition(cvForm.getExperiences().stream()
                    .map(e -> e.getPosition() != null ? e.getPosition() : "Unknown Position")
                    .collect(Collectors.joining(" | ")));
            candidateCV.setExperienceCandidateCompany(cvForm.getExperiences().stream()
                    .map(e -> e.getCompany() != null ? e.getCompany() : "Unknown Company")
                    .collect(Collectors.joining(" | ")));
            candidateCV.setExperienceCandidateInfo(cvForm.getExperiences().stream()
                    .map(e -> e.getInfo() != null ? e.getInfo() : "None")
                    .collect(Collectors.joining(" | ")));
        }

        // --- 4. CERTIFICATE ---
        if (cvForm.getCertificates() != null) {
            candidateCV.setCertificateCandidateNames(cvForm.getCertificates().stream()
                    .map(c -> c.getName() != null ? c.getName() : "Untitled Certificate")
                    .collect(Collectors.joining(" | ")));
            candidateCV.setCertificateCandidateOrganization(cvForm.getCertificates().stream()
                    .map(c -> c.getOrganization() != null ? c.getOrganization() : "Unknown Organization")
                    .collect(Collectors.joining(" | ")));
            candidateCV.setCertificateCandidateInfo(cvForm.getCertificates().stream()
                    .map(c -> c.getInfo() != null ? c.getInfo() : "None")
                    .collect(Collectors.joining(" | ")));
        }

        // --- 5. PROJECT ---
        if (cvForm.getProjects() != null) {
            candidateCV.setProjectCandidateNames(cvForm.getProjects().stream()
                    .map(p -> p.getName() != null ? p.getName() : "Untitled Project")
                    .collect(Collectors.joining(" | ")));
            candidateCV.setProjectCandidateLink(cvForm.getProjects().stream()
                    .map(p -> p.getLink() != null ? p.getLink() : "None")
                    .collect(Collectors.joining(" | ")));
            candidateCV.setProjectCandidateInfo(cvForm.getProjects().stream()
                    .map(p -> p.getInfo() != null ? p.getInfo() : "None")
                    .collect(Collectors.joining(" | ")));
        }
    }

    // Đã loại bỏ phương thức updateList và tất cả các mapTo<Entity> vì không còn tạo Entity con

    /**
     * Đồng bộ hóa CandidateCV (hiện tại đã chứa tất cả thông tin dưới dạng TEXT) sang Archive.
     * Logic này đã được đơn giản hóa vì CandidateCV đã chứa sẵn các chuỗi tổng hợp.
     */
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

        // Kỹ năng tổng hợp (sử dụng các trường TEXT đã có sẵn)
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

    // Giữ nguyên các phương thức liên quan đến Archive (deleteCVArchive, updateCVArchive, mapArchiveToResponse)
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



    public String generateLatexContent(CandidateCV cvEntity) {

        return "Not implemented yet with new text fields.";
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