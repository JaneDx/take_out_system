package com.dxx.takeOut.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dxx.takeOut.common.R;
import com.dxx.takeOut.entity.Employee;
import com.dxx.takeOut.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    //注入service，对请求进行响应
    @Autowired
    private EmployeeService employeeService;

    /**
     * 登录功能
     */
    @RequestMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){

        //1.将页面提交的密码进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2.根据页面提交的username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();//包装查询对象
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper); //得到1条数据，用getOne

        //3.如果没查询到，则返回登陆失败
        if(emp==null){
            return R.error("登录失败");
        }

        //4.查到了用户，比对密码，如果不一致，登录失败
        if(!emp.getPassword().equals(password)){
            return R.error("登录失败");
        }

        //5.密码比对成功,查看员工状态，0是禁用，1是可用
        if(emp.getStatus()==0){
            return R.error("账号已禁用");
        }

        //6.登陆成功,将员工id存入session，返回登陆成功
        HttpSession session = request.getSession();
        session.setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * 退出登陆功能
     */

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //1.清除session中保存的当前登陆员工的id
        HttpSession session = request.getSession();
        session.removeAttribute("employee");
        //2.返回结果
        return R.success("退出成功");
    }

    /**
     * 新增员工信息
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee){ //函数返回类型：如果js代码涉及到了R.data,用R<Employee>，此处只用了R.code,所以用R<String>即可
        log.info("新增员工，员工信息：{}", employee.toString());
        //补全默认的几项值
        //设置初始密码123456，需要进行md5加密处理
        String password = DigestUtils.md5DigestAsHex("123456".getBytes());
        employee.setPassword(password);
//自动填充
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        //调用mybatis plus的save方法
        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    /**
     *分页查询
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("page={}, pageSize={}, name={}",page,pageSize,name);

        //构造分页构造器
        Page pageInfo=new Page(page, pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        queryWrapper.like(!StringUtils.isEmpty(name), Employee::getName, name);  //当name不为空时，才添加这个条件
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    //根据id修改员工信息
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());

//自动填充
//        Long empId = (Long)request.getSession().getAttribute("employee");
//        employee.setUpdateUser(empId);
//        employee.setUpdateTime(LocalDateTime.now());

        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /**
     * 编辑员工信息
     * (1)先根据id查到信息，回显到页面上
     * (2)修改后，按保存，提交请求由上面 ↑ update方法响应
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息");
        Employee employee = employeeService.getById(id);
        if(employee!=null){
            return R.success(employee);
        }
        return R.error("没有查到对应员工");
    }
}
