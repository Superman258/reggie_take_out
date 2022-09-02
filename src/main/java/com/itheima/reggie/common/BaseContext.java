package com.itheima.reggie.common;

//20.基于ThreadLocal封装工具，用于保存和获取当前登录用户的ID
public class BaseContext {//作用域在某一个线程之内
    private static ThreadLocal<Long> threadLocal=new ThreadLocal<>();

    public static void setThreadLocal(Long id){//工具方法声明成静态的
        threadLocal.set(id);//设置id
    }

    public static Long getCurrent(){
        return threadLocal.get();//获取id值
    }
}
