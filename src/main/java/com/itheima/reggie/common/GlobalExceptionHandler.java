package com.itheima.reggie.common;
/*11.全局异常处理*/

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

//拦截Controller包中带有RestController和Controller注解的类
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)//异常处理方法：填入报异常的内容，这样就会按照下面定义的方法来处理出现这种异常的情况
    public R<String> exceptionHander(SQLIntegrityConstraintViolationException ex) {
        log.error(ex.getMessage());
//        发现控制台会出现 Duplicate entry字段
        if (ex.getMessage().contains(" Duplicate entry")) {//把违反约束的信息动态截取出来
            String[] split = ex.getMessage().split("");//ex.getMessage().split("")等同于：Duplicate entry 'zhangsan' for key 'employee.idx_username'
            String msg = split[2] + "已存在";
            return R.error(msg);
        }
        return R.error("失败了,未知响应");
    }

    //    29.捕获抛出的CustomException（删除失败）的异常处理
    @ExceptionHandler(CustomException.class)//异常处理方法：填入报异常的内容，这样就会按照下面定义的方法来处理出现这种异常的情况
    public R<String> exceptionHander(CustomException ex) {
        log.error(ex.getMessage());

        return R.error(ex.getMessage());//拿到异常信息
    }
}
