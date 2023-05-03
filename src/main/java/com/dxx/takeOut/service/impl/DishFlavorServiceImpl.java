package com.dxx.takeOut.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dxx.takeOut.entity.DishFlavor;
import com.dxx.takeOut.mapper.DishFlavorMapper;
import com.dxx.takeOut.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
