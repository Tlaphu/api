package com.ra.base_spring_boot.dto.resp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AccountCompanyResponse {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private boolean status;
    @JsonIgnoreProperties({
            "accounts", "jobs", "addresses"
    })
    private CompanyResponse company;
}
