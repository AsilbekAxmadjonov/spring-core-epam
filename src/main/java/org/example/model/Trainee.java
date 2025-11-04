package org.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Trainee extends User {
    @JsonProperty("dateOfBirth")
    private LocalDate dateOfBirth;
    private String address;

    public Trainee(String username, String firstName, String lastName, LocalDate dateOfBirth, String address) {
        super(username, firstName, lastName);
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }
}

