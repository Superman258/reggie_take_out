package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
//辅助Mybatis开发的,Service调用这里的Mapper来操作数据库的
public interface EmployeeMapper extends BaseMapper<Employee> {
}
