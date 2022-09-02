package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

//    50新增套餐,由于要在套餐和菜品两个表保存数据，所以用到两个表，又要在service（业务层）层扩展方法了
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        return  R.success("新增成功");
    }

//    53.套餐分页查询
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        Page<Setmeal> pageInfo=new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage=new Page<>();
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo,queryWrapper);
        //出现了和dishController一样的问题，Setmeal没有套餐分类字段(没有categoryName,但是SetmealDto有)解决方法参考步骤40

        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<Setmeal> records=pageInfo.getRecords();
        List<SetmealDto> list= records.stream().map((item)->{
            SetmealDto dto=new SetmealDto();
            BeanUtils.copyProperties(item,dto);
            Long categoryId=item.getCategoryId();//拿到每个菜品的分类id,可以去查寻数据库
            //根据分类id查到分类对象
            Category category= categoryService.getById(categoryId);//用categoryId获取的id去Category表查询对应id的菜品名称
            if (category!=null){
                String categoryName=category.getName();//把上一步查询到的分类名称赋给categoryName
                dto.setCategoryName(categoryName);//赋值categoryName
            }
            return dto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }

//    54.(批量)删除套餐,拥挤和接收前端传过来的需要删除的id数组，然后级联删除，需要操作两张表,需要在Service扩展方法
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("ids{}:",ids);
        setmealService.removeWithDish(ids);
        return R.success("删除成功");
    }

}
