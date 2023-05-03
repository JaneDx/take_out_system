package com.dxx.takeOut.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dxx.takeOut.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
    //MyBatis Plus：方法继承了BaseMapper，基本的增删改查都有
}
