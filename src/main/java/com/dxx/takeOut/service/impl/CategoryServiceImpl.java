package com.dxx.takeOut.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dxx.takeOut.common.CustomException;
import com.dxx.takeOut.entity.Category;
import com.dxx.takeOut.entity.Dish;
import com.dxx.takeOut.entity.Setmeal;
import com.dxx.takeOut.mapper.CategoryMapper;
import com.dxx.takeOut.service.CategoryService;
import com.dxx.takeOut.service.DishService;
import com.dxx.takeOut.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetMealService setMealService;
    /**
     * 根据id删除分类，在删除之前，需要判断是否关联菜品和套餐
     * @param id
     */
    @Override
    public void remove(Long id) {
        //查询当前分类是否关联了菜品，如果关联，抛出一个业务异常
        LambdaQueryWrapper<Dish> dishQueryWrapper=new LambdaQueryWrapper<>();
        dishQueryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishQueryWrapper);
        if(count1>0){
            //关联菜品，抛出一个业务异常
            throw new CustomException("当前分类关联了菜品，不能删除");
        }

        //查询当前分类是否关联了套餐，如果关联，抛出一个业务异常
        LambdaQueryWrapper<Setmeal> setMealQueryWrapper=new LambdaQueryWrapper();
        setMealQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setMealService.count(setMealQueryWrapper);
        if(count2>0){
            //关联套餐，抛出一个业务异常
            throw new CustomException("当前分类关联了套餐，不能删除");
        }

        //如果都没有关联，正常删除分类
        super.removeById(id);
    }
}
