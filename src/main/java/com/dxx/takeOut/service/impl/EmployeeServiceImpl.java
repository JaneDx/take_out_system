package com.dxx.takeOut.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dxx.takeOut.entity.Employee;
import com.dxx.takeOut.mapper.EmployeeMapper;
import com.dxx.takeOut.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
    //注入mapper，调用mapper里面的方法，操作数据库
}
