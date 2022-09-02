package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
//    51.新扩展保存方法，保存到两个表
    public void saveWithDish(SetmealDto setmealDto);

//    55.新扩展方法，去两个表比级联删除
    public void removeWithDish(List<Long> ids);
}
