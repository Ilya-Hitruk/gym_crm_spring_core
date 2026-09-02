package com.hitruk.gym.workload.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "month_summaries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "month_value", nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer summaryDuration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "year_summary_id", nullable = false)
    private YearSummary yearSummary;
}
