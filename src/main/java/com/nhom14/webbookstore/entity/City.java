package com.nhom14.webbookstore.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "city")
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", columnDefinition = "VARCHAR(255)")
    private String name;

    //bi-directional one-to-many association with District (de quen)
    @OneToMany(mappedBy = "city")
    @JsonIgnore
    private List<District> districts;

    //bi-directional one-to-many association with AccountAddress (de quen)
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "city")
    @JsonIgnore
    private List<AccountAddress> accountAddresses;
}
