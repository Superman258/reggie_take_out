package com.itheima.reggie.controller;
//表现层

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.apache.commons.lang.StringUtils;

@Slf4j
@RestController
@RequestMapping("employee")
//@RequestMapping("employee")!! 可以匹配到lhost:8080/employee/
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /*
    登录,匹配到lhost:8080/employee/login，由于是JSON形式所以加注解@RequestBody
     HttpServletRequest request将 employee对象给session一份
     */
    @PostMapping("login")
//    请求参数名和Controller方法中的对象的参数一致；请求参数名和Controller方法的参数一致即可实现后端方法中的形参和前端返回数据匹配上
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
//      1.将页面提交的密码md5加密处理,直接调用Employee继承的Serializable里的方法
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
//        2.根据提交的用户名username查询数据库.
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
//        等值比较
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
//        唯一约束，即用户名不能重复
        Employee emp = employeeService.getOne(queryWrapper);
//        3.是否查询到
        if (emp == null) {
            return R.error("登陆失败");
        }

//        4.密码比对,不一致返回失败结果
        if (!emp.getPassword().equals(password)) {//失败
            return R.error("登陆失败");
        }

//        5.查看员工状态是否为禁用
        if ((emp.getStatus() == 0)) {
            return R.error("账号已禁用");
        }
//        6.登陆成功，将员工id存入Sessiopn，并返回成功结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /*员工退出*/
    @RequestMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
//        7.清除前面过来的session的员工id,放的填employee，清除也填employee
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    //    10.新增员工
    @PostMapping//由于和@RequestMapping("employee")的路径一样，所有不用写成@PostMapping("employee")
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工，员工信息{}", employee.toString());

//        设置一个初始密码并进行MD5加密处理,时间
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        以下代码直接在MyMetaObjectHandler中统一完成自动填充
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

//        HttpServletRequest request获得当前用户的信息
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        employeeService.save(employee);//调用employeeService继承的IService接口的save方法

        return R.success("新增员工成功");
    }

    //    13.拦截器创建完成后创建分页查询的方法,发现Page中有页面需要的各种信息,从前端F12发现是一个GET请求所以加上GetMapping
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("page={},pageSize={},name={}", page, pageSize, name);

//        构造分页构造器
        Page pageInfo = new Page(page, pageSize);

//        构造一个条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();

//        添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);

        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
//        执行查询
        employeeService.page(pageInfo, queryWrapper);//employeeService继承的IService会自动封装好recode等值
        return R.success(pageInfo);
    }

    //    14.启用、禁用员工账号,依靠接受code来进行相应操作，使用泛型为String即可,从前端控制台可以发现是JSON数据。@PutMapping不用写路径，因为从前端发现路径只有..../employee
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        log.info(employee.toString());

        employee.setUpdateTime(LocalDateTime.now());//获取更新时间

        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setUpdateUser(empId);//通过request对象获取session中的用户信息
        employeeService.updateById(employee);//直接调用employeeService继承的IService接口的更新方法
        return R.success("员工信息修改成功");
//        由于页面JS问题导致传过去的数据和返回的数据ID不一致导致更新失败，精度丢失了，可以在服务器Controller给页面响应JSON数据时将long变为String，使用JacksonObjectMapper
    }

    //        17.创建一个方法接受前端修改员工信息的请求,从前端和页面发现是GET请求,且有路径变量1555844159193182210，用@GetMapping("/{id}")接收
    /*Request URL: http://localhost:8080/employee/1555844159193182210
Request Method: GET*/
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        log.info("根据数据库查询员工信息");

        //employeeService使用IService接口的getById方法来根据数据库查询员工信息并传给employee
        Employee employee= employeeService.getById(id);
        if (employee!=null) {
            return R.success(employee);//把这个json给R返回给前端
        }
        return R.error("没有查询搭配对应员工信息");
    }
}


