package com.soft.book.api.interceptor;

import com.soft.book.api.exception.AuthException;
import com.soft.book.constant.HeaderConstant;
import com.soft.book.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * 登陆拦截器 未登录直接返回
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {


    private final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);

    @Autowired
    private RedisUtil<String, Object> redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // 获取前段发来的token
        String token = request.getHeader(HeaderConstant.HEADER_TOKEN);
        // 为空或在redis获取不到 则未登录
        if (StringUtils.isBlank(token) || (redisUtil.get(token)) == null) {
            log.error("RequestUrl: {}", request.getRequestURL());
            log.error("RequestURI: {}", request.getRequestURI());
            throw new AuthException("您还未登录或登录已过期，请重新登录！");
        }

        return true;
    }

}
