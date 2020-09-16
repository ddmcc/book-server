package com.soft.book.config.interceptor;

import com.soft.book.api.interceptor.RefreshTokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 刷新token拦截器配置类
 *
 */
@Configuration
public class RefreshTokenInterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private RefreshTokenInterceptor refreshTokenInterceptor;


    /**
     * 不拦截登录和注册，退出
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 排除的接口路径将不会刷新token
        registry.addInterceptor(refreshTokenInterceptor).excludePathPatterns(
                "/login",
                "/register",
                "/logout"
        );
    }

}
