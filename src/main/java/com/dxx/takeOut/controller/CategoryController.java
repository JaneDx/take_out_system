package com.dxx.takeOut.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dxx.takeOut.common.R;
import com.dxx.takeOut.entity.Category;
import com.dxx.takeOut.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * category的控制器
 */

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        //加上@RequestBody是为了，接收传过来的json转换成category对象
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize){
        //分页构造器
        Page<Category> pageInfo=new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper();
        //添加排序条件。根据sort排序
        queryWrapper.orderByAsc(Category::getSort);//升序
        //分页查询
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 根据id删除分类
     */
    @DeleteMapping
    public R<String> delete(Long ids){
        //注意：上面的参数如果是Long id是不行的，前端发过来的请求，参数是ids，后端要对应上，否则接收不到参数！！！
        categoryService.remove(ids);
        return R.success("分类信息删除成功");
    }

    /**
     * 根据id修改分类信息
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改分类信息{}",category);
        categoryService.updateById(category);
        return R.success("修改分类信息成功");
    }

    /**
     * 根据条件（type）获取所有分类
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){ //String type
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);//如果sort排序相同，再根据更新时间降序
        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }
}
