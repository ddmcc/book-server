package com.soft.book.entity.bo;

import lombok.Data;

/**
 * 用户实体类
 */
@Data
public class User {

    private Integer userId;

    private String userName;

    private String password;

    private String confirm;
}
