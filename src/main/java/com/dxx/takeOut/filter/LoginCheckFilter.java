package com.dxx.takeOut.filter;

import com.alibaba.fastjson.JSON;
import com.dxx.takeOut.common.BaseContext;
import com.dxx.takeOut.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//检查用户是否已经登陆
@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")  //urlPatterns表示过滤哪些页面
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER=new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //1.获取本次请求的URI
        String requestURI = request.getRequestURI();
        log.info("拦截到{}",requestURI);

        //定义不需要过滤的url
        String[] urls=new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**", //静态资源
                "/front/**" , //移动端静态资源
                "/user/login",//移动端登陆
                "/user/sendMsg"//移动端发送短信
        };
        //2.判断本次请求是否需要处理
        boolean check = check(requestURI, urls);

        //3.如果不需要处理，直接放行
        if(check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        //4.需要处理：判断登陆状态，如果已登录，放行
        Object empId = request.getSession().getAttribute("employee");
        if(empId!=null){
            log.info("用户已登录");

            //把id存到threadLocal中，为了自动填充updateUser/createUser
            BaseContext.setCurrentId((Long)empId);

            filterChain.doFilter(request,response);
            return;
        }
        //4.需要处理：判断移动端登陆状态，如果已登录，放行
        Object userId = request.getSession().getAttribute("user");
        if(userId!=null){
            log.info("前端用户已登录");

            //把id存到threadLocal中，为了自动填充updateUser/createUser
            BaseContext.setCurrentId((Long)userId);

            filterChain.doFilter(request,response);
            return;
        }
        //5.如果未登录，返回未登录结果，前端js代码会自动跳转到login
        //通过输出流方式向客户端页面响应数据, 把R对象转成json对象，再通过输出流写回去
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        log.info("用户未登录");
        return;
    }

    //路径匹配，检查本次请求是否需要放行
    public boolean check(String requestURI, String[] urls) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }
}
