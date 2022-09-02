package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class DishServiceImp extends ServiceImpl<DishMapper, Dish> implements DishService {
//    39.实现同时保存对应的口味信息,从dish和dish_flavor中取出来保存到dishDto

    @Autowired
    private DishFlavorService dishFlavorService;
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到dish表
        this.save(dishDto);

        Long dishId = dishDto.getId();//获取菜品id

        List<DishFlavor> flavors=dishDto.getFlavors();//菜品口味集合

//        用流的方式来遍历集合取出口味，也可以用flavor.forEach(),遍历出来的每一个DishFlavor实体就是item
        flavors=flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());//Collectors.toList()使得取出来后又转化为list集合

        //保存口味到dish_flavor,saveBatch:批量保存,是个集合，括号内写集合获得的方法
        dishFlavorService.saveBatch(flavors);
    }

    //    43.实现根据id查询对应的菜品信息和口味信息，方便修改,然后就可以在Controller中调service的这个方法
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //先查询菜品的基本信息，用getById(id)就会按照id从dish表查询对应数据(继承的IService)
        //this.getById(id)==dishService.getById(id);
        Dish dish=this.getById(id);

        DishDto dishDto=new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //再查询口味信息，从dish_flavor
        QueryWrapper<DishFlavor> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("dish_id",dish.getId());
        List<DishFlavor> flavors=dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

//    47.实现修改的更新操作
    @Transactional//保证事务一致性
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表,由于dishDto是dish的子类，所以传进来也能更新dish表
        this.updateById(dishDto);
        //更新口味表:先删除原先ID对应的口味信息，再插入ID的对应口味信息
        QueryWrapper<DishFlavor> queryWrapper=new QueryWrapper<>();
        DishFlavor dishFlavor=new DishFlavor();
        queryWrapper.eq("dish_id",dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors=flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());//Collectors.toList()使得取出来后又转化为list集合
        dishFlavorService.saveBatch(flavors);
    }
}










