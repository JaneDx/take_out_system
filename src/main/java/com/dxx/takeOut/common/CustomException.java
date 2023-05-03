package com.dxx.takeOut.common;


/**
 * 自定义的异常消息类
 * 此处处理，删除分类时，关联菜品或者套餐，不能删除的情况
 */
public class CustomException extends RuntimeException{
    public CustomException(String message){
        super(message);
    }
}
