package com.dxx.takeOut.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dxx.takeOut.common.CustomException;
import com.dxx.takeOut.common.R;
import com.dxx.takeOut.dto.SetmealDto;
import com.dxx.takeOut.entity.Category;
import com.dxx.takeOut.entity.Dish;
import com.dxx.takeOut.entity.Setmeal;
import com.dxx.takeOut.entity.SetmealDish;
import com.dxx.takeOut.service.CategoryService;
import com.dxx.takeOut.service.SetMealService;
import com.dxx.takeOut.service.SetmealDishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetMealService setMealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;


    /**
     * 新增套餐
     */
    @Transactional //涉及到多张表的处理，开启事务
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setMealService.save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) { //设置setmeal_id
            setmealDish.setSetmealId(setmealDto.getId());
        }
        setmealDishService.saveBatch(setmealDishes);
        return R.success("新增套餐成功");
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        Page pageInfo=new Page<>(page,pageSize);
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setMealService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 删除套餐,只能删除停售套餐，如果套餐正在售卖，不能删
     */
    @Transactional //涉及到多张表的处理，开启事务
    @DeleteMapping
    public R<String> delete(Long[] ids){
        for (Long id : ids) {
            LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(Setmeal::getId,id);
            Setmeal setmeal = setMealService.getOne(queryWrapper);
            if(setmeal.getStatus()==0){  //已经停售
                setMealService.remove(queryWrapper);
                LambdaQueryWrapper<SetmealDish> queryWrapper1=new LambdaQueryWrapper<>();
                queryWrapper1.eq(SetmealDish::getSetmealId,id);
                setmealDishService.remove(queryWrapper1);
            }
            else {//套餐正在售卖，抛出业务异常
                throw new CustomException("此商品正在售卖，不能删除");
            }
        }

        return R.success("删除套餐成功");
    }

    /**
     * 修改套餐售卖状态
     */
    @PostMapping("/status/{s}")
    public R<String> update(@PathVariable int s, Long[] ids){

        for (Long id : ids) {
            LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(Setmeal::getId,id);
            Setmeal setmeal = setMealService.getOne(queryWrapper);
            if(s==0){
                setmeal.setStatus(0);
            }
            else{
                setmeal.setStatus(1);
            }
            setMealService.updateById(setmeal);
        }
        return R.success("修改套餐售卖状态成功");
    }

    /**
     * 根据id查询套餐
     */
    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id){
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getId,id);
        Setmeal setmeal = setMealService.getOne(queryWrapper);

        LambdaQueryWrapper<SetmealDish> queryWrapper1=new LambdaQueryWrapper<>();
        queryWrapper1.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper1);

        SetmealDto setmealDto=new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);
        setmealDto.setSetmealDishes(setmealDishes);

        return R.success(setmealDto);
    }
    /**
     * 修改套餐信息
     */
    @Transactional //涉及到多张表的处理，开启事务
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setMealService.updateById(setmealDto);//更新setmeal表

        LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);//先把原先那些菜删掉

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealDto.getId());
            setmealDishService.save(setmealDish);//再把新的菜品加进去
        }
        return R.success("修改套餐成功");
    }

    /**
     * 展示同一类套餐list
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setMealService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 前端页面获取套餐里面的所有菜
     */
    @GetMapping("/dish/{id}")
    public R<List<SetmealDish>> getSetMeal(@PathVariable Long id){
        LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);

        return R.success(list);
    }
}
