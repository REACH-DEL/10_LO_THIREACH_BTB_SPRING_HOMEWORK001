package org.hrd._10_lo_thireach_spring_homework_001.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaginationInfo {
    private int totalElements;
    private int currentPage;
    private int pageSize;
    private int totalPages;
}
