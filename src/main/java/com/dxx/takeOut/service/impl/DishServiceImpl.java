package com.dxx.takeOut.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dxx.takeOut.dto.DishDto;
import com.dxx.takeOut.entity.Dish;
import com.dxx.takeOut.entity.DishFlavor;
import com.dxx.takeOut.mapper.DishMapper;
import com.dxx.takeOut.service.DishFlavorService;
import com.dxx.takeOut.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    /**
     * 新增菜品，同时插入菜品对应的口味数据
     */
    @Override
    @Transactional //涉及到多张表的处理，开启事务
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);

        //保存口味到dish_flavor
        Long dishId=dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
        }
        dishFlavorService.saveBatch(flavors);
    }
}
