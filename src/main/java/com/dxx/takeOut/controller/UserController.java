package com.dxx.takeOut.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dxx.takeOut.common.R;
import com.dxx.takeOut.entity.User;
import com.dxx.takeOut.service.UserService;
import com.dxx.takeOut.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 发送手机验证码
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();
        if(phone!=null){
            //生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode4String(4);

            //调用短信服务API，发送短信
            log.info("登陆验证码为{}",code);
            //需要将生成的验证码保存到session
            session.setAttribute(phone,code);
            return R.success("发送验证码成功");
        }
        return R.error("短信发送失败");
    }

    /**
     * 登陆
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){//用map接收前端传来的手机号和验证码
        //获取手机号
        String reg ="^1[3,4,5,6,7,8,9][0-9]{9}$";
        String phone = map.get("phone").toString();
        Pattern pattern = Pattern.compile(reg);//函数语法 匹配的正则表达式
        Matcher matcher = pattern.matcher(phone);//进行匹配
        if(!matcher.matches()){
            return R.error("请输入正确的手机号");
        }
        //获取验证码
        if(map.get("code")==null){
            return R.error("请输入验证码");
        }
        String code = map.get("code").toString();

        //从session中获取验证码
        String code1 = session.getAttribute(phone).toString();

        //对比两个验证码，如果一致，登陆
        if(code1!=null&&code.equals(code1)){
            //如果表里没有这个用户，自动注册
            LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if(user==null){
                user=new User();
                user.setPhone(phone);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("验证码不正确");
    }

    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest request){
        //清除session中登陆的当前用户id
        HttpSession session = request.getSession();
        session.removeAttribute("user");
        return R.success("退出成功");
    }
}
