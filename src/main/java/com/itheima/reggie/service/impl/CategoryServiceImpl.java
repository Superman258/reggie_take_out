package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    //注入菜品
    @Autowired
    private DishService dishService;

    //注入套餐
    @Autowired
    private SetmealService setmealService;

    @Override
//    27.实现,根据id删除分类，删除前判断是否关联菜单
    public void remove(Long id) {
        //27.1构造查询条件,直接调用框架来操作数据库，查询当前分类是否关联菜品，若关联则抛出一个业务异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        //27.2添加查询条件,使用等值查询根据id进行查询(eq方法相当于赋值“=”，Dish::getCategoryId就是实例化一个Dish对象，id是PathValue传进来的id
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);

        int count1=dishService.count(dishLambdaQueryWrapper);//等同于数据库语句：select count(*) from dish where category_id=Dish::getCategoryId，看看关联的数目

        //27.3判断当前分类是否关联菜品，若关联则抛出一个业务异常
        if (count1>0){//关联了菜品，抛出异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        //查询当前分类是否关联套餐，若关联则抛出一个业务异常，写法同上
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper=new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2=setmealService.count();
        if (count2>0){//关联了套餐，抛出异常
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }

        //若进行到这说明没有关联，则正常删除
        super.removeById(id);

    }
}
