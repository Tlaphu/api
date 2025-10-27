package com.ra.base_spring_boot.dto.resp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStats {
    private Long liveJobs;
    private Long companies;
    private Long candidates;
    private Long newJobs;
}