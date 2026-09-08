package com.hitruk.gym.workload.api.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YearSummaryDto {
    private Integer year;
    private List<MonthSummaryDto> months;
}
