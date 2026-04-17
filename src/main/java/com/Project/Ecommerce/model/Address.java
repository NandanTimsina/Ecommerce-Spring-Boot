package com.Project.Ecommerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@ToString
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer addressId;

    @NotBlank
    @Size(min = 5,message = "Street name must be at least 5 char long.")
    private String street;

    @NotBlank
    @Size(min = 5,message = "Building name must be at least 5 char long.")
    private String buildingName;

    @NotBlank
    @Size(min = 2,message = "city name must be at least 2 char long.")
    private String city;

    @NotBlank
    @Size(min = 2,message = "state name must be at least 2 char long.")
    private String state;
    @NotBlank
    @Size(min = 3,message = "Country name must be at least 2 char long.")
    private String country;

    @NotBlank
    @Size(min = 4,message = "pin code must be at least 4 char long.")
    private String pincode;

    @ToString.Exclude
    @ManyToMany(mappedBy = "addresses")
    private List<User> users=new ArrayList<>();

    public Address(String pincode, String country, String state, String city, String buildingName, String street) {
        this.pincode = pincode;
        this.country = country;
        this.state = state;
        this.city = city;
        this.buildingName = buildingName;
        this.street = street;
    }
}
