package com.soft.book.entity.bo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Book {

    private Integer bookId;

    private String bookName;

    private String author;

    private String press;

    private Date publicationDate;

    private String introduction;

    private BigDecimal price;

    private String pages;

    private String binding;

    private String isbn;

    private String image;

    private Long count;

    private BigDecimal score;

}
