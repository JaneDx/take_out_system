package com.dxx.takeOut.common;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

//全局异常处理器，基于代理
@Slf4j
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody  //返回json数据，所以写上
public class GlobalExceptionHandler {

    //异常处理方法
    //此处 处理添加用户/类别时，username重复/name重复（一些不允许重复的值，重复了）

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.info(ex.getMessage());
        if(ex.getMessage().contains("Duplicate entry")){
            String[] s = ex.getMessage().split(" ");
            return R.error(s[2]+"已存在");
        }
        return R.error("未知错误");
    }

    /**
     * 处理自定义的异常消息，显示在页面上
     * 此处处理，删除分类时，关联菜品或者套餐，不能删除的情况
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex){
        log.info(ex.getMessage());
        return R.error(ex.getMessage());
    }

}
