package com.soft.book.mapper;

import com.soft.book.entity.bo.User;
import org.apache.ibatis.annotations.Param;

/**
 * 用户mapper
 */
public interface UserMapper {

    /**
     * 根据用户名查询用户信息
     * @param userName  用户名
     * @return          User
     */
    User getByUsername(String userName);

    /**
     * 根据用户id查询用户名
     *
     * @param id        id
     * @return          用户名
     */
    String getNameById(Integer id);

    /**
     * 保存用户
     *
     * @param userName      用户名
     * @param password      密码
     * @return              插入数量 正常返回 1
     */
    int save(@Param("userName") String userName, @Param("password") String password);
}
