package com.ra.base_spring_boot.dto.resp;

import lombok.Builder;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class CandidateCVResponse {
   
    private Long id;
    private String name;
    private Date dob;
    private String address;
    private Integer gender;
    private String link;
    private String description;

    private String development;
    private String template;
    private String title;
    private String candidateTitle;
    private List<String> projects;
    private List<String> skills;
    private List<String> educations;
    private List<String> experiences;
    private List<String> certificates;
}