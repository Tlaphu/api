package com.ra.base_spring_boot.dto.resp;

import lombok.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String gender;
    private Date dob;
    private String link;
    private boolean status;
    private Integer isOpen;
    private Date created_at;
    private Date updated_at;
}
