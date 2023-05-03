package com.dxx.takeOut.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dxx.takeOut.entity.User;
import com.dxx.takeOut.mapper.UserMapper;
import com.dxx.takeOut.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
