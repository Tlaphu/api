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
public class CompanyResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String logo;
    private String website;
    private String link_fb;
    private String link_linkedin;
    private String link_Github;
    private Integer follower;
    private Integer size;
    private String description;
    private Date created_at;
    private Date updated_at;
    private String CompanyPolicy;

    @JsonIgnoreProperties({"company"})
    private List<AddressCompanyResponse> addresses;

    private String typeCompanyName;
}
