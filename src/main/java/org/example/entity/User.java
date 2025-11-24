package org.example.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.entity.converter.PasswordConverter;


@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"id"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    @Convert(converter = PasswordConverter.class)
    @ToString.Exclude
    private char[] password;

    @Column(nullable = false)
    private Boolean isActive;
}

