package com.soft.book.service.impl;


import com.soft.book.entity.bo.User;
import com.soft.book.mapper.UserMapper;
import com.soft.book.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {


    @Autowired
    private UserMapper userMapper;


    /**
     * 根据用户名查询用户信息
     *
     * @param userName 用户名
     * @return          User
     */
    @Override
    public User getByUserName(String userName) {
        return userMapper.getByUsername(userName);
    }

    /**
     * 根据用户id查询用户名
     *
     * @param id id
     * @return 用户名
     */
    @Override
    public String getNameById(Integer id) {
        return userMapper.getNameById(id);
    }

    /**
     * 注册
     *
     * @param userName 用户名
     * @param password 密码
     * @return true/false
     */
    @Override
    public boolean register(String userName, String password) {
        // 插入成功返回1 大于0插入成功
        return userMapper.save(userName, password) > 0;
    }
}
