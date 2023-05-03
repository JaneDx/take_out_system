package com.dxx.takeOut.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dxx.takeOut.entity.Setmeal;
import com.dxx.takeOut.mapper.SetMealMapper;
import com.dxx.takeOut.service.SetMealService;
import org.springframework.stereotype.Service;

@Service
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, Setmeal> implements SetMealService {
}
