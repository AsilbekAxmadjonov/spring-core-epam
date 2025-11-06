package org.example.model;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Trainee extends User {

    private LocalDate dateOfBirth;
    private String address;

    public Trainee(String username, String firstName, String lastName, LocalDate dateOfBirth, String address) {
        super(username, firstName, lastName);
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }
}
