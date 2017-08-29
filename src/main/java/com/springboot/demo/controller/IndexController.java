package com.springboot.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/user")
public class IndexController {
	 //logback
    private final static Logger logger = LoggerFactory.getLogger(IndexController.class);
    /**
     * 初始化登录页面
     * @return
     */
    @RequestMapping(value = "/login_view",method = RequestMethod.GET,produces= "text/plain;charset=UTF-8")
    public String login_view(){
//    	logger.debug("记录debug日志");
//        logger.info("访问了index方法");
//        logger.error("记录error错误日志");
        return "login";
    }
    
//    @RequestMapping(value = "/login",method = RequestMethod.GET)
//    public JSONObject login(HttpServletRequest request,String name) throws Exception
//    {
//        JSONObject obj = new JSONObject();
//        obj.put("msg","用户："+name+"，登录成功。");
//        //将返回值写入到请求对象中
//        request.setAttribute(LoggerUtils.LOGGER_RETURN,obj);
//        return obj;
//    }
//    
    @RequestMapping(value = "/index",method = RequestMethod.GET,produces= "text/plain;charset=UTF-8")
    public String index(){
        return "index";
    }
}
