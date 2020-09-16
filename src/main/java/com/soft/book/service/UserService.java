package com.soft.book.service;

import com.soft.book.entity.bo.User;

/**
 * 用户service接口
 */
public interface UserService {


    /**
     * 根据用户名查询用户信息
     *
     * @param userName      用户名
     * @return              UserVO
     */
    User getByUserName(String userName);


    /**
     * 根据用户id查询用户名
     *
     * @param id        id
     * @return          用户名
     */
    String getNameById(Integer id);

    /**
     * 注册
     *
     * @param userName      用户名
     * @param password      密码
     * @return              true/false
     */
    boolean register(String userName, String password);

}
