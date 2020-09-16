package com.soft.book.entity;

import com.soft.book.entity.bo.Comment;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class Detail implements Serializable {


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

    private Map<String, BigDecimal> scores;
}
