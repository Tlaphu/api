package com.ra.base_spring_boot.dto.resp;

import lombok.Data;
import lombok.*;
import java.util.List;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class JobCandidateResponse {

    private Long id;

    private Long jobId;
    private String jobTitle;
    private String companyName; // <--- ĐÃ THÊM: Tên Công ty

    private Long candidateId;
    private String candidateName;
    private String candidateTitle;
    private String candidateAddress;

    private String logoCandidate;

    private String skillcandidateName;

    private Boolean isAccepted;
    private Long cvId;
    private String cvFileUrl; // <--- ĐÃ THÊM: URL/Đường dẫn file CV
    private String cvTitle;   // <--- ĐÃ THÊM: Tiêu đề CV
    private String cover_letter;
    private String status;

}