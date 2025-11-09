package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.FormCandidateCV;
import com.ra.base_spring_boot.dto.req.FormCandidateCVArchive;
import com.ra.base_spring_boot.model.CandidateCV;
import com.ra.base_spring_boot.model.CandidateCVArchive;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ICandidateCVService {

    CandidateCV createNewCV(FormCandidateCV cvForm, Long candidateId);
    
    CandidateCV updateCV(Long cvId, FormCandidateCV cvForm, Long candidateId);
    
    CandidateCV getCVById(Long cvId, Long candidateId);
    
    List<CandidateCV> getAllCVsByCandidate(Long candidateId);

    void deleteCV(Long cvId, Long candidateId);

    void clearAllCandidateDetails(Long candidateId);

    void deleteCVArchive(Long archiveId);
    CandidateCVArchive updateCVArchive(Long archiveId, FormCandidateCVArchive updateForm);
}