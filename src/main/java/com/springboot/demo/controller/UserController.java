package com.springboot.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.demo.entity.UserEntity;
import com.springboot.demo.service.UserService;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 查询用户列表方法
     * @return
     */
    @RequestMapping(value ="/list" ,method = RequestMethod.GET,produces= "text/plain;charset=UTF-8")
    public List<UserEntity> list(){
        return userService.list();
    }

    /**
     * 添加、更新用户方法
     * @param entity
     * @return
     */
    @RequestMapping(value = "/save",method = RequestMethod.GET,produces= "text/plain;charset=UTF-8")
    public UserEntity save(UserEntity entity)
	{
    	UserEntity userEntity = new UserEntity();
	    userEntity.setName("测试");
	    userEntity.setAddress("测试地址");
	    userEntity.setAge(21);
	    userService.save(userEntity);
        return null;
    }

    /**
     * 删除用户方法
     * @param id 用户编号
     * @return
     */
    @RequestMapping(value = "/delete",method = RequestMethod.GET,produces= "text/plain;charset=UTF-8")
    public String delete(Long id)
    {
        userService.delete(id);
        return "用户信息删除成功";
    }

    @RequestMapping(value = "/age")
    public List<UserEntity> age(int age){
        return userService.nativeQuery(age);
    }
    /**
     * 分页查询测试
     * @param page 传入页码，从1开始
     * @return
     */
    @RequestMapping(value = "/cutpage")
    public List<UserEntity> cutPage(int page)
    {
        UserEntity user = new UserEntity();
        user.setSize(2);
        user.setSord("desc");
        user.setPage(page);
        
        return userService.cutPage(user);
    }

}
