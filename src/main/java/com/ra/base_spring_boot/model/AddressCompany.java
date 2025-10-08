package com.ra.base_spring_boot.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @ManyToOne
    @JoinColumn(name = "company_id")
    @JsonBackReference
    private Company company;
    private String address;
    private String map_url;
    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;
    
}