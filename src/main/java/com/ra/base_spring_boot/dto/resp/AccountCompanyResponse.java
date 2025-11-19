package com.ra.base_spring_boot.dto.resp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.Date;
import java.util.List;

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
    private Integer gender;
    private Date dob;
    private boolean status;
    private boolean isPremium;
    @JsonIgnoreProperties({
            "accounts", "jobs", "addresses"
    })
    private CompanyResponse company;
    private List<AddressCompanyResponse> addresses;
}
