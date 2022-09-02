package com.itheima.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;

    private Integer status;


//    18.@TableField
    @TableField(fill = FieldFill.INSERT_UPDATE)//INSERT_UPDATE表示插入和更新时填充字段
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT)//只有插入时填充
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)//只有插入时填充
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)//INSERT_UPDATE表示插入和更新时填充字段
    private Long updateUser;

}
