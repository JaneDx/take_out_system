package com.dxx.takeOut.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dxx.takeOut.common.BaseContext;
import com.dxx.takeOut.common.CustomException;
import com.dxx.takeOut.common.R;
import com.dxx.takeOut.dto.OrderDto;
import com.dxx.takeOut.entity.*;
import com.dxx.takeOut.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService orderDetailService;
    /**
     * 提交订单
     */
    @PostMapping("/submit")
    @Transactional //涉及到多表
    public R<String> submit(@RequestBody Orders orders){
        Long currentId = BaseContext.getCurrentId();

        //获取购物车中，要下单的菜品
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        List<ShoppingCart> dishes = shoppingCartService.list(queryWrapper);
        if(dishes==null){
            throw new CustomException("购物车为空，不能下单");
        }
        //查询用户数据
        User user = userService.getById(currentId);

        //查询地址信息
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if(addressBook==null){
            throw new CustomException("地址有误，不能下单");
        }
        //使用IdWorker生成订单号
        long orderId = IdWorker.getId();

        //计算金额, 补全订单菜品细节信息
        AtomicInteger amount = new AtomicInteger(0);//原子操作，保证多线程、高并发的情况下，不会出错，保证线程安全
        for (ShoppingCart dish : dishes) {
            OrderDetail orderDetail=new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(dish.getNumber());
            orderDetail.setDishFlavor(dish.getDishFlavor());
            orderDetail.setDishId(dish.getDishId());
            orderDetail.setSetmealId(dish.getSetmealId());
            orderDetail.setName(dish.getName());
            orderDetail.setImage(dish.getImage());
            orderDetail.setAmount(dish.getAmount());
            //计算金额，单价*份数
            amount.addAndGet(dish.getAmount().multiply(new BigDecimal(dish.getNumber())).intValue());
            //向订单明细表插入所有菜品、套餐
            orderDetailService.save(orderDetail);
        }

        //补全order里面的属性
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额,需要计算
        orders.setUserId(currentId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());  //收货人
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        //向订单表插入数据
        orderService.save(orders);

        //清空购物车数据
        shoppingCartService.remove(queryWrapper);
        return R.success("下单成功");
    }

    /**
     * 查看订单
     */
    @GetMapping("/userPage")
    public R<Page> get(int page, int pageSize){
        Page<Orders> pageInfo=new Page<>(page,pageSize);
        Page<OrderDto> pageOrderInfo=new Page<>(page,pageSize);

        LambdaQueryWrapper<Orders> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId,BaseContext.getCurrentId());
        queryWrapper.orderByDesc(Orders::getOrderTime);

        List<Orders> orders = orderService.page(pageInfo, queryWrapper).getRecords();

        List<OrderDto> list=new ArrayList<>();
        for (Orders order : orders) {
            OrderDto dto=new OrderDto();
            BeanUtils.copyProperties(order,dto);

            String orderId = order.getNumber();
            LambdaQueryWrapper<OrderDetail> queryWrapper1=new LambdaQueryWrapper<>();
            queryWrapper1.eq(OrderDetail::getOrderId,orderId);
            List<OrderDetail> orderDetails = orderDetailService.list(queryWrapper1);
            dto.setOrderDetails(orderDetails);

            list.add(dto);
        }
        BeanUtils.copyProperties(pageInfo,pageOrderInfo);
        pageOrderInfo.setRecords(list);
        return R.success(pageOrderInfo);
    }
}
