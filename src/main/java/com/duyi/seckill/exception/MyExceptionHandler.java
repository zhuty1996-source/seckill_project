package com.duyi.seckill.exception;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class MyExceptionHandler implements HandlerExceptionResolver {



    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception ex) {

        Map<String,Object> model = new HashMap<String,Object>();
        model.put("ex",ex);
        ex.printStackTrace();

        //根据不同错误转向不同页面
        if (ex instanceof UserException){
            return new ModelAndView("error",model);
        }else {
            return new ModelAndView("error",model);
        }

    }
}
