package com.itheima.reggie.filter;
/*检查用户是否已经完成登录*/

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.sun.deploy.net.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.PathMatcher;

//urlPatterns表示拦截路径内容，filterName随便写
@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*")
@Slf4j//表示用日志方式输出，里面有log.info 方法
public class LoginCheckFilter implements Filter {

    //    专门用于路径匹配
    public static final AntPathMatcher Path_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

//        8.获取本次请求的URI(web上每一种可用的资源，如 HTML文档、图像、视频片段、程序等都由一个URI进行标识的,URL是URI的一个子集)
        String requestURI = request.getRequestURI();

//        拦截到请求
        log.info("拦截到请求：{}", requestURI);
//        把不需要处理的请求放行
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**"
        };

//        判断是否需要处理
        boolean check = check(urls, requestURI);
        if (check) {//需要处理,filterChain.doFilter是过滤器
            filterChain.doFilter(request, response);
            return;
        }

//        通过session对象判断是否已经登录，是则放行，不需要处理
        if (request.getSession().getAttribute("employee") != null) {//已经登陆的情况
            log.info("用户已登录，用户ID为", request.getSession().getAttribute("employee"));


//            21.取得当前用户id，并放在empId，再存入线程
            Long empId=(Long) request.getSession().getAttribute("employee");
            BaseContext.setThreadLocal(empId);

            filterChain.doFilter(request, response);
            return;
        }

//              未登录则返回登录请求，通过输出流的方式像客服端页面响应数据
//             把R.error("NOTLOGIN")转为JSON，再通过输出流回到前端
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

    }


    //    9.路径匹配，是否需要放行，把上面的urls拿过来
    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = Path_MATCHER.match(url, requestURI);
            if (match)
                return match;
        }
        return false;

    }

}
