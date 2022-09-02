package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;

public interface DishService extends IService<Dish> {
//    38.由于新增菜品要操作两张表，所以需要扩展一个方法 ，（同时插入菜品对应的口味数据需要操作dish和dishflavor表）
    public void saveWithFlavor(DishDto dishDto);

//    42.由于需要用到两张表，所以需要扩展一个方法:根据id查询对应的菜品信息和口味信息，方便修改
    public DishDto getByIdWithFlavor(Long id);

//    46.更新菜品信息，同时更新口味信息，所以又是操作两个表
    public void   updateWithFlavor(DishDto dishDto);
}
