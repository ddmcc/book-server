package com.soft.book.config.interceptor;

import com.soft.book.api.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * 登录检查拦截器配置类
 */
@Configuration
public class AuthInterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;


    /**
     * 不拦截登录，注册，登录状态获取，首页列表
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor).excludePathPatterns(
                "/login",
                "/error",
                "/register",
                "/loginStatus",
                "/books",
                "/logout",
                "/books/**",
                "/comments/**",
                "/comments",
                "/tags"
        );
    }

}
