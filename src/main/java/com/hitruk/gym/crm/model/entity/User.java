package com.hitruk.gym.crm.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
public abstract class User implements BaseEntity<Long> {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private Boolean isActive;
}
