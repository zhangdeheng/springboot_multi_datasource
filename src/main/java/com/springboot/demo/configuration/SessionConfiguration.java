package com.springboot.demo.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.springboot.demo.interceptor.SessionInterceptor;

@Configuration
public class SessionConfiguration extends WebMvcConfigurerAdapter
{
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SessionInterceptor()).addPathPatterns("/**");
    }
}
