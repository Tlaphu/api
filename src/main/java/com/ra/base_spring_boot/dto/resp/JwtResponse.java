package com.ra.base_spring_boot.dto.resp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ra.base_spring_boot.model.AccountCompany;
import com.ra.base_spring_boot.model.Candidate;
import com.ra.base_spring_boot.model.Company;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class JwtResponse {
    private String accessToken;
    private final String type = "Bearer";

    @JsonIgnoreProperties({"roles","password"})
    private Candidate candidate;

    @JsonIgnoreProperties({
            "password",
            "roles",
            "company.accounts",
            "company.jobs",
            "company.addresses"
    })
    private AccountCompanyResponse accountCompany;
    private Set<String> roles;
}
