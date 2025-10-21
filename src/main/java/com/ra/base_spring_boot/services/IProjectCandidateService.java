package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.resp.ProjectCandidateResponse;
import com.ra.base_spring_boot.dto.req.FormProjectCandidate;

import java.util.List;

public interface IProjectCandidateService {
    List<ProjectCandidateResponse> getProject();
    ProjectCandidateResponse createProject(FormProjectCandidate req);
    ProjectCandidateResponse updateProject(Long id, FormProjectCandidate req);
    void deleteProject(Long id) ;
}
