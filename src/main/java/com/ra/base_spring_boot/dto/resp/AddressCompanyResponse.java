package com.ra.base_spring_boot.dto.resp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressCompanyResponse {
    private Long id;
    private String address;
    private String mapUrl;
    private String locationName;
}

