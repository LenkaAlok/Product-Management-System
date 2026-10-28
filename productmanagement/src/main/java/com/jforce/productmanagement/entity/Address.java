package com.jforce.productmanagement.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many addresses can belong to one user
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Street is required")
    @Column(nullable = false)
    private String street;

    @NotBlank(message = "City is required")
    @Column(nullable = false)
    private String city;


    @NotBlank(message = "State is required")
    @Column(nullable = false)
    private String state;

    // Indian six-digit postal code
    @NotBlank(message = "Postal code is required")
    @Pattern(regexp = "\\d{6}", message = "Postal code must contain 6 digits")
    @Column(nullable = false, length = 6)
    private String postalCode;


    @NotBlank(message = "Country is required")
    @Column(nullable = false, length = 50)
    private String country;
}

