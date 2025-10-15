package com.ra.base_spring_boot.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FormAddressCompany {

    @NotBlank(message = "address not null")
    private String address;

    private String mapUrl;

    @NotNull(message = "Location ID not null")
    private Long locationId;
}
