package org.example.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@SuperBuilder
public class Trainer extends User {
    private String specialization;

    public Trainer(String username, String firstName, String lastName, String specialization) {
        super(username, firstName, lastName);
        this.specialization = specialization;
    }
}
