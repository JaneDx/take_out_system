package com.dxx.takeOut.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dxx.takeOut.common.R;
import com.dxx.takeOut.dto.DishDto;
import com.dxx.takeOut.entity.Dish;
import com.dxx.takeOut.entity.DishFlavor;
import com.dxx.takeOut.service.CategoryService;
import com.dxx.takeOut.service.DishFlavorService;
import com.dxx.takeOut.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品
     */

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize,String name){
        Page<Dish> pageInfo=new Page<>(page, pageSize);
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.like(name!=null,Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo,queryWrapper);

        //加分类name
        Page<DishDto> dtoPageInfo=new Page<>();
        BeanUtils.copyProperties(pageInfo,dtoPageInfo,"records"); //把pageInfo拷贝到dtoPageInfo,除了records

        List<Dish> records = pageInfo.getRecords();//原先查到的dish
        List<DishDto> newRecords=new LinkedList<>();//一会儿要返回的加上类别名称的 dtoDish

        for (Dish record : records) { //每一个record加分类名称
            Long categoryId = record.getCategoryId();
            String categoryName = categoryService.getById(categoryId).getName();//查分类名

            DishDto dishDto=new DishDto();
            BeanUtils.copyProperties(record,dishDto);
            dishDto.setCategoryName(categoryName);
            newRecords.add(dishDto);
        }
        dtoPageInfo.setRecords(newRecords);
        return R.success(dtoPageInfo);
    }

    /**
     * 根据id得到菜品信息及其口味
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id){
        Dish dish = dishService.getById(id);

        //类别名称
        Long categoryId = dish.getCategoryId();
        String categoryName = categoryService.getById(categoryId).getName();

        //口味
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);

        DishDto dishDto=new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        dishDto.setCategoryName(categoryName);
        dishDto.setFlavors(list);

        return R.success(dishDto);
    }

    /**
     * 修改菜品及其口味
     */
    @Transactional //涉及到多张表的处理，开启事务
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        //更新口味表。下面这样做，修改口味没有问题，但是删除或者新增口味就不好使
        List<DishFlavor> flavors = dishDto.getFlavors();
//        for (DishFlavor flavor : flavors) {
//            dishFlavorService.updateById(flavor);
//        }
        Long id = dishDto.getId();//菜id

        //就把原来的口味都删了，再重新增加
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,id);
        dishFlavorService.remove(queryWrapper);

        //新增口味
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(id); //得到的口味没有dish_id, 要设置一下
            dishFlavorService.save(flavor);
        }

        //更新dish表
        dishService.updateById(dishDto);  //因为dishDto继承了dish，所以直接传dishDto没问题
        return R.success("修改菜品成功");
    }

    /**
     * 修改售卖状态
     */
    @PostMapping("/status/{s}")
    public R<String> updateStatus(@PathVariable int s, Long[] ids){
        for (Long id : ids) {
            Dish dish = dishService.getById(id);
            if(s==0){
                dish.setStatus(0);
            }
            else if(s==1){
                dish.setStatus(1);
            }
            dishService.updateById(dish);
        }

        return R.success("修改售卖状态成功");
    }
    /**
     * 删除菜品
     */
    @DeleteMapping
    public R<String> delete(Long[] ids){
        for (Long id : ids) {
            LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper();
            queryWrapper.eq(Dish::getId,id);
            dishService.remove(queryWrapper);
        }

        return R.success("删除菜品成功");
    }

    /**
     * 根据条件查询对应的菜品
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){  //传过来的参数虽然只是categoryId，但是用dish对象来接收条件，更具有普适性
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        //categoryId根据菜品类别查
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        //只查状态是1的，正在售卖的
        queryWrapper.eq(Dish::getStatus,1);
        //根据菜品名搜
        queryWrapper.like(dish.getName()!=null,Dish::getName,dish.getName());

        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dtoList = new ArrayList<>();
        //把查到的dish list改造成dishDto list，加上口味以及类名
        for (Dish d : list) {
            DishDto dto=new DishDto();
            BeanUtils.copyProperties(d,dto);

            Long categoryId = d.getCategoryId();
            dto.setCategoryName(categoryService.getById(categoryId).getName());

            Long dishId = d.getId();
            LambdaQueryWrapper<DishFlavor> queryWrapper1=new LambdaQueryWrapper<>();
            queryWrapper1.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> flavors = dishFlavorService.list(queryWrapper1);
            dto.setFlavors(flavors);
            dtoList.add(dto);
        }
        return R.success(dtoList);
    }

//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){  //传过来的参数虽然只是categoryId，但是用dish对象来接收条件，更具有普适性
//        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
//        //categoryId根据菜品类别查
//        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
//        //只查状态是1的，正在售卖的
//        queryWrapper.eq(Dish::getStatus,1);
//        //根据菜品名搜
//        queryWrapper.like(dish.getName()!=null,Dish::getName,dish.getName());
//
//        //添加排序条件
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        List<Dish> list = dishService.list(queryWrapper);
//
//        return R.success(list);
//    }
}
