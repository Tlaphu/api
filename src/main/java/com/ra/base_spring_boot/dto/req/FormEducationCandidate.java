package com.ra.base_spring_boot.dto.req;

import lombok.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class FormEducationCandidate {
<<<<<<< HEAD
    private Long id;
    private long candidateId;
    private String name_education;
=======
    private String nameEducation;
>>>>>>> 1f30a81d790ecab57c3ee282eea67f509f150ff1
    private String major;
    private String gpa;
    private Date startedAt;
    private Date endAt;
    private String info;
}


