package com.springboot.demo.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.demo.entity.LoggerEntity;
public interface LoggerJPA
        extends JpaRepository<LoggerEntity,Long>
{

}
