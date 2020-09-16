package com.soft.book.api.interceptor;

import com.soft.book.constant.HeaderConstant;
import com.soft.book.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;


/**
 *
 * 拦截除登录 注册 所有请求，刷新token
 *
 */
@Component
public class RefreshTokenInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisUtil<String, Object> redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        // 获取前段发来的token
        String token = request.getHeader(HeaderConstant.HEADER_TOKEN);
        // 不为空并且还未过期
        Object userId;
        if (StringUtils.isNotBlank(token) && ((userId = redisUtil.get(token))) != null) {
            // 刷新
            redisUtil.set(token, userId, 30, TimeUnit.MINUTES);
        }

        return true;
    }

}
