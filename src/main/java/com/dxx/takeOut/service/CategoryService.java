package com.dxx.takeOut.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dxx.takeOut.entity.Category;

public interface CategoryService extends IService<Category> {
   public void remove(Long id);
}
