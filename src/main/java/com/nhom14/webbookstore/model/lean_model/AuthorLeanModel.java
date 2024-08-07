package com.nhom14.webbookstore.model.lean_model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorLeanModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int id;

    private String name;

    private String bio;
}
