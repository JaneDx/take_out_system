package com.dxx.takeOut.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dxx.takeOut.entity.OrderDetail;
import com.dxx.takeOut.mapper.OrderDetailMapper;
import com.dxx.takeOut.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
