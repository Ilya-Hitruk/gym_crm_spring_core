package com.hitruk.gym.workload.api.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthSummaryDto {
    private Integer month;
    private Integer summaryDuration;
}
