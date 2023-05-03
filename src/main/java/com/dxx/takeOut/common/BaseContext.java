package com.dxx.takeOut.common;

//基于threadLocal封装工具类，用户保存和获取当前登陆用户id
public class BaseContext {
    //用户id是long型，所以泛型要用long
    private static ThreadLocal<Long> threadLocal=new ThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
