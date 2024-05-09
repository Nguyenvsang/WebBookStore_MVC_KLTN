package com.nhom14.webbookstore.model.lean_model;

import com.nhom14.webbookstore.model.response_model.BookResponseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemLeanModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int id;

    private int quantity;

    private BookResponseModel book;
}
