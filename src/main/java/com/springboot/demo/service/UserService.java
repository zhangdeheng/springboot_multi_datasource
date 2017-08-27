package com.springboot.demo.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.springboot.demo.entity.UserEntity;
import com.springboot.demo.jpa.UserJPA;

@Service
public class UserService {
	@Autowired
    private UserJPA userJPA;
	public List<UserEntity> list(){
        return userJPA.findAll();
    }
	@Transactional
	public UserEntity save(UserEntity entity)
    {
        return userJPA.save(entity);
    }
	@Transactional
	public List<UserEntity> delete(Long id)
    {
		userJPA.delete(id);
        return userJPA.findAll();
    }
	public List<UserEntity> nativeQuery(int age){
        return userJPA.nativeQuery(age);
    }
    public List<UserEntity> cutPage(UserEntity user){

        //获取排序对象
        Sort.Direction sort_direction = Sort.Direction.ASC.toString().equalsIgnoreCase(user.getSord()) ? Sort.Direction.ASC : Sort.Direction.DESC;
        //设置排序对象参数
        Sort sort = new Sort(sort_direction, user.getSidx());
        //创建分页对象
        PageRequest pageRequest = new PageRequest(user.getPage() - 1,user.getSize(),sort);
        //执行分页查询
        return userJPA.findAll(pageRequest).getContent();
    }
}
