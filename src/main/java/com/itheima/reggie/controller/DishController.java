package com.itheima.reggie.controller;
//34.菜品管理（实体类Dish和DishFlavor的相关操作都在这个Controller实现）
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;
//    36.新增菜品
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){//操作两张表!
        //因为dish类缺少flavors的属性，所以形参不能用Dish，新导入一个DishDto类用于封装页面提交的数据。DTO：即数据传输对象，用于展示层和服务层的数据传输
        dishService.saveWithFlavor(dishDto);

        return  R.success("新增成功");
    }

//    40.菜品管理页面分页查询菜品
//Request URL: http://localhost:8081/dish/page?page=1&pageSize=10&name=     Request Method: GET
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构建分页构造器
        Page<Dish> pageInfo=new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage=new Page<>();
        //条件构造器,用QueryWrapper,不用Lambda语法
        QueryWrapper<Dish> queryWrapper=new QueryWrapper<>();
        queryWrapper.like(name != null,"name",name);
        //排序条件:按照更新时间降序
        queryWrapper.orderByDesc("update_time");

        /*LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(name != null,Dish::getName,name);
        //排序条件:按照更新时间降序
        queryWrapper.orderByDesc(Dish::getUpdateTime);*/

        //执行继承的IService的page方法,此时pageInfo中会赋有值，可以用于对象copy
        dishService.page(pageInfo,queryWrapper);

        /*由于菜品管理中的菜品分类不在Dish实体类中，返回给前端的是DishId，而DishDto中有categoryName去对应
        其余的属性在前端都做好了对应，比如prop="name",label="菜品名称",即name对应菜品名称*/

        //对象copy,可以不用拷贝records属性，下面会单独去处理records
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records=pageInfo.getRecords();
        List<DishDto> list= records.stream().map((item)->{
            //遍历records的list对象，用map（）取出records对象。然后创建dto对象，完事后记得用collectt收集，再转list
            DishDto dishDto=new DishDto();

            BeanUtils.copyProperties(item,dishDto);//item赋值给dishDto，就可以拿到除菜品名称外的其他基本数据

            Long categoryId=item.getCategoryId();//拿到每个菜品的分类id,可以去查寻数据库
            Category category= categoryService.getById(categoryId);//用categoryId获取的id去Category表查询对应id的菜品名称

            if (category!=null){
                String categoryName=category.getName();//把上一步查询到的分类名称赋给categoryName
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

//    41.修改菜品信息
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable  Long id){//根据id查询口味信息和菜品信息,又需要查询两种表，需要去DishService表扩展方法
//44.调用service写的方法
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

//    45.保存修改的菜品信息，其实和新增菜品一样
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){//操作两张表!
        //因为dish类缺少flavors的属性，所以形参不能用Dish，新导入一个DishDto类用于封装页面提交的数据。DTO：即数据传输对象，用于展示层和服务层的数据传输
        dishService.updateWithFlavor(dishDto);
        return  R.success("新增成功");
    }

//    49.新增套餐时，根据条件（Id）查询菜品数据
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){

//        QueryWrapper<Dish>queryWrapper=new QueryWrapper<>();
//        queryWrapper.eq(dish.getCategoryId()!=null,"category_id",dish.getCategoryId());
//        queryWrapper.orderByAsc("sort").orderByDesc("update_time");
        LambdaQueryWrapper<Dish>queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);

        //59.改造菜品数据，用DishDto来增加口味信息
        List<DishDto> dishDtoList= list.stream().map((item)->{
            //遍历records的list对象，用map（）取出records对象。然后创建dto对象，完事后记得用collectt收集，再转list
            DishDto dishDto=new DishDto();

            BeanUtils.copyProperties(item,dishDto);//item赋值给dishDto，就可以拿到除菜品名称外的其他基本数据

            Long categoryId=item.getCategoryId();//拿到每个菜品的分类id,可以去查寻数据库
            Category category= categoryService.getById(categoryId);//用categoryId获取的id去Category表查询对应id的菜品名称

            if (category!=null){
                String categoryName=category.getName();//把上一步查询到的分类名称赋给categoryName
                dishDto.setCategoryName(categoryName);
            }

            Long dishId = item.getId();//获取当前菜品id
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper=new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            //SQL:select *from dish_flavor where dish_id=item.getId
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);//封装成集合对象
            // 用上面的集合对象给传进来的dishDto赋值
            dishDto.setFlavors(dishFlavorList);

            return dishDto;
        }).collect(Collectors.toList());
        return R.success(dishDtoList);
    }
}


















