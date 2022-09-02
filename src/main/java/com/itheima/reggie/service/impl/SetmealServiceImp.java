package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImp extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;

    @Override
//52.实现保存到两个表的方法
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐信息，操作setmeal表，执行insert
        this.save(setmealDto);

        //保存套餐和菜品的关联信息，操作setmeal_dish表，执行insert,setmealDto有除了SetmealId外的数据
        List<SetmealDish> setmealDish=setmealDto.getSetmealDishes();
        setmealDish.stream().map((item)->{//循环把setmealDto.getId()给SetmealDish表的id
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDish);
    }

//    56.实现级联删除具体逻辑如下
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //只有停售状态下可以删除,所以需要查询套餐状态。select count(*) from setmeal where id in (ids) and status =1
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        int count=this.count(queryWrapper);

        //不能删除则抛出一个业务异常
        if (count>0){
            throw new CustomException("套餐售卖中无法删除");
        }

        //可以删除则先直接删除套餐表setmeal中的数据
        this.removeByIds(ids);      //this就是setmealServiceImp

        /*删除关系表setmea_dish中的数据
        由于传过来的id是setmeal表的ids，不是setmea_dish的主键值.所以不能直接用setmealDishService.removeByIds();
        需要LambdaQueryWrapper用ids找SetmealDish的主键id
        delete frorm setmeal_dish where setmeal_id in (ids)*/
        LambdaQueryWrapper<SetmealDish>lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getDishId,ids);
        setmealDishService.remove(lambdaQueryWrapper);
    }
}














