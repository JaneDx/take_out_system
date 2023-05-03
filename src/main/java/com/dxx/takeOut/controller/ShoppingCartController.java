package com.dxx.takeOut.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dxx.takeOut.common.BaseContext;
import com.dxx.takeOut.common.R;
import com.dxx.takeOut.entity.ShoppingCart;
import com.dxx.takeOut.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 加入购物车
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        Long currentId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);

        Long dishId = shoppingCart.getDishId();
        if(dishId!=null){
            //加入购物车的是菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }
        else{
            //加入购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        //查询购物车中是否已经存在本菜品
        ShoppingCart shoppingCart1 = shoppingCartService.getOne(queryWrapper);
        if(shoppingCart1!=null){
            //如有，数量+1即可
            Integer number = shoppingCart1.getNumber();
            shoppingCart1.setNumber(number+1);
            shoppingCartService.updateById(shoppingCart1);
        }
        else{
            //没有，加入本菜品
            shoppingCart.setNumber(1);
            shoppingCart.setUserId(currentId);
            shoppingCartService.save(shoppingCart);
            shoppingCart1=shoppingCart;
        }
        return R.success(shoppingCart1);
    }

    /**
     * 删除1份菜品/套餐
     */
    @PostMapping("/sub")
    public R sub(@RequestBody ShoppingCart shoppingCart){
        Long currentId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);

        Long dishId = shoppingCart.getDishId();
        if(dishId!=null){
            //删除的是菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }
        else{
            //删除的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        //如果删除完数量为0，要删除数据
        ShoppingCart shoppingCart1 = shoppingCartService.getOne(queryWrapper);
        Integer number = shoppingCart1.getNumber();
        if(number==1){
            //在数据库中删除
            shoppingCartService.removeById(shoppingCart1);
            return R.success("sub成功");
        }
        else{
            //数量 - 1
            shoppingCart1.setNumber(number-1);
            shoppingCart1.setUserId(currentId);
            shoppingCartService.updateById(shoppingCart1);
            return R.success(shoppingCart1);
        }

    }

    /**
     * 查看购物车
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        Long currentId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        shoppingCartService.remove(queryWrapper);
        return R.success("清空成功");
    }
}
