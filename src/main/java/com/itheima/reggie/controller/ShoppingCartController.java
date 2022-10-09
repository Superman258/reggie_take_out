package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;


//    61.添加到购物车，前端页面会返回json数据，是菜品则传过来dishId，是套餐则传过来setmealId
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        //设置用户id，指定哪个用户使用购物车
        Long current = BaseContext.getCurrent();
        shoppingCart.setUserId(current);

        //查询当前用户的菜品或者套餐，若存在，则原来的数量加一，不存在则设为一
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,shoppingCart.getUserId());
        if(shoppingCart.getDishId()!=null){
            //要加入购物车的是菜品
            queryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        } else {
            //是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        //SQL:select * from shopping_cart where user_id=? and dish_id/setmeal_id=shoppingCart,查询菜品和套餐是否在购物车
        //getOne():查出唯一的一条数据
        ShoppingCart shoppingCartServiceOne = shoppingCartService.getOne(queryWrapper);
        if(shoppingCartServiceOne!=null){//找到了数据，即菜品/套餐存在，加一
            Integer number = shoppingCartServiceOne.getNumber();
            shoppingCartServiceOne.setNumber(number+1);
            shoppingCartService.updateById(shoppingCartServiceOne);
        }else {//未找到，菜品不存在，设为一
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            shoppingCartServiceOne=shoppingCart;
        }
        return R.success(shoppingCartServiceOne);
    }

//    62.查看购物车
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrent());//查询该用户的购物车信息
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

//    63.清空购物车
    @DeleteMapping("/clean")
    public R<String> clean(){
        //SQL:delete from shopping_cart where user_id=
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrent());
        shoppingCartService.remove(queryWrapper);
        return R.success("清空成功");
    }
}
