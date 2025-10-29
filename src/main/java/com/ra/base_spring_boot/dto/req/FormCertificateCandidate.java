package com.ra.base_spring_boot.dto.req;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormCertificateCandidate {

    String name;
    String organization;
    private Date started_at;
    private Date end_at;
    private String info;
}
