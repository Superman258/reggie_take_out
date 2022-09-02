package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//22.创建控制层CategoryController
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    //@Autowired:spring自动将匹配到的属性值进行注入

    @Autowired
    private CategoryService categoryService;


//    23.新增菜品分类.
    @PostMapping//路径也是category
    public R<String> save(@RequestBody Category category){//创建一个保存方法，可以直接调用CategoryService继承的方法
        log.info("category:{}",category);
        categoryService.save(category);
        return R.success("新增成功");
    }

//    24.菜品分页信息查询
    @GetMapping("/page")//获得前端数据
    public R<Page> page(Integer page,Integer pagesize){

        //分页构造器
        Page<Category> pageInfo=new Page<>();//先用MP提供的API创建分页查询对象

        //需要设置排序条件，所以需要条件构造器
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
        //添加排序条件，根据sort进行排序(升序语法)
        queryWrapper.orderByAsc(Category::getSort);
        //用service调用Mapper进行查询
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }


//    25.根据id删除分类 路径上是.../id?...，所以这里形参填id就可以正常接收到
    @DeleteMapping
    public R<String> delete(Long id){
        log.info("删除分类");
        //categoryService.removeById(id);//可以直接使用继承自IService接口的删除方法
        categoryService.remove(id);//调用步骤27里的自定义删除方法
        return R.success("分类信息删除成功");
    }

//    30.根据修改分类信息:Request URL: http://localhost:8080/category  Request Method: PUT
    @PutMapping
    public R<String> update(Category category){
        log.info("修改分类信息");

        categoryService.updateById(category);
        //利用步骤19的元对象处理器，自动填充更新时间等信息
        return  R.success("修改信息成功");
    }

//    35.新增菜品时前端页面会返回4个请求，在这接收这四个请求即可
    @GetMapping("/list")//通过接收前端Ajax传来的type值来展示菜品分类/套餐分类下拉框
    public R<List<Category>> list(Category category){//根据条件查询分类数据
        //构建条件构造器
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();

        //添加查询条件,去数据库查询：当category.getType存在时构造一个Category category，把等于category.getType（前端页面传过来的）的值查询出来
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());

        //排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByAsc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);

        return R.success(list);
    }
}
