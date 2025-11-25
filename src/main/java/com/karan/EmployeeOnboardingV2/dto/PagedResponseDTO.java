package com.karan.EmployeeOnboardingV2.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PagedResponseDTO {
    private List<?> data;
    private int currentPage;
    private int pageSize;
    private long totalElements;
    private long totalPages;
    private boolean last;
}
