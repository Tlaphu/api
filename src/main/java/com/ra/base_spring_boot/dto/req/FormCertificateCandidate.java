package com.ra.base_spring_boot.dto.req;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class FormCertificateCandidate {
<<<<<<< HEAD
    private Long id;
    String name;
    String organization;
=======
    private String name;
    private String organization;
>>>>>>> 1f30a81d790ecab57c3ee282eea67f509f150ff1
    private Date started_at;
    private Date end_at;
    private String info;
}
