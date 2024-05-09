package com.nhom14.webbookstore.model.response_model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponseModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int id;

    private int quantity;

    private BookResponseModel book;
}
