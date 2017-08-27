package com.springboot.demo.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class StaticUrlConfiguration extends WebMvcConfigurerAdapter
{
    //自定义静态资源文件路径
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/virtualexperiment/resources/**").addResourceLocations("classpath:/static/");
    }
}
