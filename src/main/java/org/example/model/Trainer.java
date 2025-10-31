package org.example.model;

public class Trainer extends User {
    private String specialization;

    public Trainer() {
        super();
    }

    public Trainer(String username, String firstName, String lastName, String specialization) {
        super(username, firstName, lastName);
        this.specialization = specialization;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    @Override
    public String toString() {
        return String.format("Trainer: %s %s (%s), Specialization: %s",
                getFirstName(), getLastName(), getUsername(), specialization);
    }
}
