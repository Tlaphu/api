package com.ra.base_spring_boot.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class FormUpdateCompany {
    private String name;
    private String logo;
    private String phone;
    private String website;
    private String linkFb;
    private String linkLinkedin;
    private String link_Github;
    private String description;
    private String CompanyPolicy;
    private Integer size;
    private Integer follower;
}

