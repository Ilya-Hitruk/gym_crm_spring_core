package com.hitruk.gym.workload.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "trainer_workloads")
@CompoundIndex(name = "first_name_last_name_idx", def = "{'firstName': 1, 'lastName': 1}")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerWorkloadDocument {

    @Id
    private String username;

    private String firstName;

    private String lastName;

    private Boolean isActive;

    @Builder.Default
    private List<YearSummary> years = new ArrayList<>();
}
