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

import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateCVServiceImpl implements ICandidateCVService {

    private final ICandidateRepository candidateRepository;
    private final ICandidateCVRepository candidateCVRepository;
    
    private final IProjectRepository projectCandidateRepository;
    private final ISkillsCandidateRepository skillsCandidateRepository;
    private final IEducationCandidateRepository educationCandidateRepository;
    private final IExperienceCandidateRepository experienceCandidateRepository;
    private final ICertificateCandidateRepository certificateCandidateRepository;

    public CandidateCVResponse mapToResponse(CandidateCV candidateCV) {

        Candidate candidate = candidateCV.getCandidate();

        List<String> projectNames = candidateCV.getProjectCandidates().stream()
                .map(p -> p.getName() != null ? p.getName() : (p.getInfo() != null ? p.getInfo() : p.getLink()))
                .collect(Collectors.toList());

        List<String> skillNames = candidateCV.getSkillCandidates().stream()
                .map(SkillsCandidate::getName)
                .collect(Collectors.toList());

        List<String> educationNames = candidateCV.getEducationCandidates().stream()
                .map(EducationCandidate::getNameEducation)
                .collect(Collectors.toList());

        List<String> experienceNames = candidateCV.getExperienceCandidates().stream()
                .map(e -> e.getPosition() + " at " + e.getCompany())
                .collect(Collectors.toList());

        List<String> certificateNames = candidateCV.getCertificateCandidates().stream()
                .map(CertificateCandidate::getName)
                .collect(Collectors.toList());

        return CandidateCVResponse.builder()
                .id(candidate.getId())
                .name(candidate.getName())
                .dob(candidate.getDob())
                .address(candidate.getAddress())
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

    @Override
    @Transactional
    public CandidateCV createNewCV(FormCandidateCV cvForm, Long candidateId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new HttpBadRequest("Candidate not found with ID: " + candidateId));

        CandidateCV newCV = CandidateCV.builder()
                .title(cvForm.getTitle())
                .template(cvForm.getTemplate())
                .candidate(candidate)
                .created_at(new Date())
                .updated_at(new Date())
                .build();

        newCV = candidateCVRepository.save(newCV);

        updateAllCVDetails(newCV, cvForm);

        return candidateCVRepository.save(newCV);
    }

    @Override
    @Transactional
    public CandidateCV updateCV(Long cvId, FormCandidateCV cvForm, Long candidateId) {
        CandidateCV existingCV = candidateCVRepository.findByIdAndCandidate_Id(cvId, candidateId)
                .orElseThrow(() -> new HttpBadRequest("CV not found or does not belong to this candidate."));

        existingCV.setTitle(cvForm.getTitle());
        existingCV.setTemplate(cvForm.getTitle());
        existingCV.setUpdated_at(new Date());

        updateAllCVDetails(existingCV, cvForm);

        return candidateCVRepository.save(existingCV);
    }

    private void updateAllCVDetails(CandidateCV candidateCV, FormCandidateCV cvForm) {

        updateList(candidateCV.getProjectCandidates(), cvForm.getProjects(),
                (FormProjectCandidate dto) -> mapToProjectCandidate(dto, candidateCV));

        updateList(candidateCV.getSkillCandidates(), cvForm.getSkills(),
                (FormSkillsCandidate dto) -> mapToSkillCandidate(dto, candidateCV));

        updateList(candidateCV.getEducationCandidates(), cvForm.getEducations(),
                (FormEducationCandidate dto) -> mapToEducationCandidate(dto, candidateCV));

        updateList(candidateCV.getExperienceCandidates(), cvForm.getExperiences(),
                (FormExperienceCandidate dto) -> mapToExperienceCandidate(dto, candidateCV));

        updateList(candidateCV.getCertificateCandidates(), cvForm.getCertificates(),
                (FormCertificateCandidate dto) -> mapToCertificateCandidate(dto, candidateCV));
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

        candidateCVRepository.delete(cv);
    }

    @Override
    @Transactional
    public void clearAllCandidateDetails(Long candidateId) {
        List<CandidateCV> cvList = candidateCVRepository.findByCandidate_Id(candidateId);
        candidateCVRepository.deleteAll(cvList);
    }

    private <E, D> void updateList(List<E> entityList, List<D> dtoList, Function<D, E> mapper) {
        if (dtoList != null) {
            entityList.clear();
            List<E> newEntities = dtoList.stream().map(mapper).collect(Collectors.toList());
            entityList.addAll(newEntities);
        }
    }

    private ProjectCandidate mapToProjectCandidate(FormProjectCandidate dto, CandidateCV candidateCV) {
        
        if (dto.getId() != null) {
            ProjectCandidate existing = projectCandidateRepository.findById(dto.getId())
                .orElseThrow(() -> new HttpBadRequest("Project ID not found: " + dto.getId()));
            
            if (dto.getName() != null) {
                existing.setName(dto.getName());
            }
            if (dto.getInfo() != null) {
                existing.setInfo(dto.getInfo());
            }
            if (dto.getLink() != null) {
                existing.setLink(dto.getLink());
            }
            existing.setUpdated_at(new Date());
            return existing;
        }
        
        return ProjectCandidate.builder()
                .name(dto.getName())
                .info(dto.getInfo())
                .link(dto.getLink())
                .candidateCV(candidateCV)
                .candidate(candidateCV.getCandidate())
                .created_at(new Date())
                .updated_at(new Date())
                .build();
    }

    private SkillsCandidate mapToSkillCandidate(FormSkillsCandidate dto, CandidateCV candidateCV) {
        
        if (dto.getId() != null) {
            SkillsCandidate existing = skillsCandidateRepository.findById(dto.getId())
                .orElseThrow(() -> new HttpBadRequest("Skill ID not found: " + dto.getId()));
            
            if (dto.getName() != null) {
                existing.setName(dto.getName());
            }
            existing.setUpdated_at(new Date());
            return existing;
        }

        return SkillsCandidate.builder()
                .name(dto.getName())
                .candidateCV(candidateCV)
                .candidate(candidateCV.getCandidate())
                .created_at(new Date())
                .updated_at(new Date())
                .build();
    }

    private EducationCandidate mapToEducationCandidate(FormEducationCandidate dto, CandidateCV candidateCV) {
        
        if (dto.getId() != null) {
            EducationCandidate existing = educationCandidateRepository.findById(dto.getId())
                .orElseThrow(() -> new HttpBadRequest("Education ID not found: " + dto.getId()));
            
            if (dto.getNameeducation() != null) {
                existing.setNameEducation(dto.getNameeducation());
            }
            if (dto.getMajor() != null) {
                existing.setMajor(dto.getMajor());
            }
            if (dto.getStartedAt() != null) {
                existing.setStartedAt(dto.getStartedAt());
            }
            if (dto.getEndAt() != null) {
                existing.setEndAt(dto.getEndAt());
            }
            if (dto.getInfo() != null) {
                existing.setInfo(dto.getInfo());
            }
            if (dto.getGpa() != null) {
                existing.setGpa(dto.getGpa());
            }
            existing.setUpdatedAt(new Date()); 
            return existing;
        }
        
        return EducationCandidate.builder()
                .nameEducation(dto.getNameeducation()) 
                .major(dto.getMajor())
                .startedAt(dto.getStartedAt())
                .endAt(dto.getEndAt())
                .info(dto.getInfo())
                .gpa(dto.getGpa()) 
                .candidateCV(candidateCV)
                .candidate(candidateCV.getCandidate())
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();
    }

    private ExperienceCandidate mapToExperienceCandidate(FormExperienceCandidate dto, CandidateCV candidateCV) {
        
        if (dto.getId() != null) {
            ExperienceCandidate existing = experienceCandidateRepository.findById(dto.getId())
                .orElseThrow(() -> new HttpBadRequest("Experience ID not found: " + dto.getId()));
            
            if (dto.getPosition() != null) {
                existing.setPosition(dto.getPosition());
            }
            if (dto.getCompany() != null) {
                existing.setCompany(dto.getCompany());
            }
            // SỬA LỖI: Gọi snake_case getter từ DTO (vì DTO có started_at/end_at)
            if (dto.getStarted_at() != null) {
                existing.setStarted_at(dto.getStarted_at());
            }
            if (dto.getEnd_at() != null) {
                existing.setEnd_at(dto.getEnd_at());
            }
            if (dto.getInfo() != null) {
                existing.setInfo(dto.getInfo());
            }
            existing.setUpdated_at(new Date());
            return existing;
        }

        return ExperienceCandidate.builder()
                .position(dto.getPosition())
                .company(dto.getCompany())
                .started_at(dto.getStarted_at())
                .end_at(dto.getEnd_at())
                .info(dto.getInfo())
                .candidateCV(candidateCV)
                .candidate(candidateCV.getCandidate())
                .created_at(new Date())
                .updated_at(new Date())
                .build();
    }

    private CertificateCandidate mapToCertificateCandidate(FormCertificateCandidate dto, CandidateCV candidateCV) {
        
        if (dto.getId() != null) {
            CertificateCandidate existing = certificateCandidateRepository.findById(dto.getId())
                .orElseThrow(() -> new HttpBadRequest("Certificate ID not found: " + dto.getId()));
            
            if (dto.getName() != null) {
                existing.setName(dto.getName());
            }
            if (dto.getOrganization() != null) {
                existing.setOrganization(dto.getOrganization());
            }
            // SỬA LỖI: Gọi snake_case getter từ DTO (vì DTO có started_at/end_at)
            if (dto.getStarted_at() != null) {
                existing.setStarted_at(dto.getStarted_at());
            }
            if (dto.getEnd_at() != null) {
                existing.setEnd_at(dto.getEnd_at());
            }
            if (dto.getInfo() != null) {
                existing.setInfo(dto.getInfo());
            }
            existing.setUpdated_at(new Date());
            return existing;
        }

        return CertificateCandidate.builder()
                .name(dto.getName())
                .organization(dto.getOrganization())
                .started_at(dto.getStarted_at())
                .end_at(dto.getEnd_at())
                .info(dto.getInfo())
                .candidateCV(candidateCV)
                .candidate(candidateCV.getCandidate())
                .created_at(new Date())
                .updated_at(new Date())
                .build();
    }
}
