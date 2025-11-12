package org.example.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class Trainee extends User {
    private LocalDate dateOfBirth;
    private String address;
}
