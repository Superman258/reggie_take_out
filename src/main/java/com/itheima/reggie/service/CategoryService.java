package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Category;

public interface CategoryService extends IService<Category> {
//    26. 菜品的Service层的接口上自己定义一个可以判断关联的删除方法,并在实现类上实现
    public void remove(Long id);
}
