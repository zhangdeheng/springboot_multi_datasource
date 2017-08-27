package com.springboot.demo.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.springboot.demo.interceptor.LoggerInterceptor;

@Configuration
public class LoggerConfguration extends WebMvcConfigurerAdapter
{
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoggerInterceptor()).addPathPatterns("/**");
    }
}
