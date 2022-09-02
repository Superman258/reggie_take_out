package com.itheima.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

//19.自定义元数据对象处理器，用于自动填充
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    //metaObject存放的都是页面传过来的数据
    public void insertFill(MetaObject metaObject) {//插入时触发自动填充，把EmployeeController的第十步再次实现，不然数据就是空的
        log.info("公共字段自动填充【insert】");
        log.info(metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());//等同employee.setCreateTime(LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());//等同employee.setUpdateTime(LocalDateTime.now());
        metaObject.setValue("createUser", BaseContext.getCurrent());//等同employee.setCreateUser(empId);
        metaObject.setValue("updateUser", BaseContext.getCurrent());//等同employee.setUpdateUser(empId);

    }

    @Override
    public void updateFill(MetaObject metaObject) {//更新时触发自动填充，就不需要createTime和createUser了
        log.info("公共字段自动填充【update】");
        log.info(metaObject.toString());
        metaObject.setValue("updateTime", LocalDateTime.now());//等同employee.setUpdateTime(LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrent());//等同employee.setUpdateUser(empId);

    }
}
