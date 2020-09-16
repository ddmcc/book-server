package com.soft.book.entity.bo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Comment {

    private Integer bookId;

    private Integer commentId;

    private String commentContent;

    private Integer userId;

    private String userName;

    private Date createTime;

    private BigDecimal score;

}
