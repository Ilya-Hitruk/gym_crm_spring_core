package com.hitruk.gym.workload.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YearSummary {

    private Integer year;

    @Builder.Default
    private List<MonthSummary> months = new ArrayList<>();
}
