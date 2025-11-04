package org.example.model;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.time.LocalDate;

public class Trainee extends User{

    @JsonAlias("birthDate")
    private LocalDate dateOfBirth;

    private String address;

    public Trainee() {
        super();
    }

    public Trainee(String username, String firstName, String lastName, LocalDate dateOfBirth, String address) {
        super(username, firstName, lastName);
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
