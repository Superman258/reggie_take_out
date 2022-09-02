package com.itheima.reggie.common;

//28. 由于CategoryServiceImpl需要抛异常，所以这里定义一个业务层的异常类

public class CustomException extends RuntimeException{
    public CustomException(String message){
        super(message);//调取父类的方法对message的一些操作
    }
}
