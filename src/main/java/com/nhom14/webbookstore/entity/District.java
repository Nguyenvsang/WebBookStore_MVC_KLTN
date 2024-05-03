package com.nhom14.webbookstore.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "district")
public class District {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", columnDefinition = "VARCHAR(255)")
    private String name;

    //bi-directional many-to-one association with City
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "city_id")
    private City city;

    //bi-directional one-to-many association with Ward (de quen)
    @OneToMany(mappedBy = "district")
    @JsonIgnore
    private List<Ward> wards;

    //bi-directional one-to-many association with AccountAddress (de quen)
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "district")
    @JsonIgnore
    private List<AccountAddress> accountAddresses;
}
