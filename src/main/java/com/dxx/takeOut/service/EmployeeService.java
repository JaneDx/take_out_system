package com.dxx.takeOut.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dxx.takeOut.entity.Employee;

public interface EmployeeService extends IService<Employee> {
    //IService也是由MyBatis Plus提供的
    //定义要进行的增删改查操作
}
