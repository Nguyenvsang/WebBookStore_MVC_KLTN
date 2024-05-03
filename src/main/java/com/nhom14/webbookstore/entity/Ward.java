package com.nhom14.webbookstore.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "ward")
public class Ward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", columnDefinition = "VARCHAR(255)")
    private String name;

    //bi-directional many-to-one association with District
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "district_id")
    private District district;

    //bi-directional one-to-many association with AccountAddress (de quen)
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "ward")
    @JsonIgnore
    private List<AccountAddress> accountAddresses;
}
