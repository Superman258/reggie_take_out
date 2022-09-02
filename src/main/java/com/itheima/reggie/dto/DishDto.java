package com.itheima.reggie.dto;

import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;
//37.导入dto。私有属性不能继承，但是可以通过子类继承父类的方法来访问父类的私有属性
@Data
public class DishDto extends Dish {//扩展了一些属性，当Dish没有对应属性时接收页面提交的参数

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
