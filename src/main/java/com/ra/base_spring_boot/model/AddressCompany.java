package com.ra.base_spring_boot.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AddressCompany {
    @Id
    private String id;
    private String company_id;
    private String address;
    private String map_url;
    private String location_id;
}