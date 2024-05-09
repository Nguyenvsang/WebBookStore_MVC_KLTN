package com.nhom14.webbookstore.model.response_model;

import com.nhom14.webbookstore.model.lean_model.BookAuthorLeanModel;
import com.nhom14.webbookstore.model.lean_model.BookImageLeanModel;
import com.nhom14.webbookstore.model.lean_model.CategoryLeanModel;
import com.nhom14.webbookstore.model.lean_model.DiscountLeanModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookResponseModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private double costPrice;
    private double sellPrice;
    private CategoryLeanModel category;
    private String publisher;
    private String description; // mô tả sơ lược
    private int status; //hoạt động:1, ngừng kd:0
    private String detail; // chi tiết nội dung
    private int quantity;
    private List<BookImageLeanModel> bookImages;
    private List<BookAuthorLeanModel> bookAuthors;
    private DiscountLeanModel currentDiscount;
}
