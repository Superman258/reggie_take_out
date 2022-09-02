package com.itheima.reggie.service;
//业务层
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Employee;

//Controller调Sevice来查询数据库，只需要在用到的地方写上 @Autowired private EmployeeService employeeService;
public interface EmployeeService extends IService<Employee> {
}
