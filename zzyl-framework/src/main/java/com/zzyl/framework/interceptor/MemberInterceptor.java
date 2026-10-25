package com.zzyl.framework.interceptor;

import cn.hutool.core.map.MapUtil;
import com.zzyl.common.utils.UserThreadLocal;
import com.zzyl.framework.web.service.TokenService;
import io.jsonwebtoken.Claims;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MemberInterceptor implements HandlerInterceptor {

    @Autowired
    private TokenService tokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断是否是请求接口
        if (!(handler instanceof HandlerMethod)){
            //如果为true,则请求的不是接口,而是静态资源,直接返回true放行
            return true;
        }
        //获取token
        String token = request.getHeader("authorization");
        if (token == null || token.isEmpty()){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new RuntimeException("请先登录");
        }
        //解析token,判读非空
        Claims claims = tokenService.parseToken(token);
        if (claims == null || claims.isEmpty()){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new RuntimeException("请先登录");
        }
        //解析token共享的信息
        Long userId = MapUtil.get(claims, "userId", Long.class);
        if (userId == null){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new RuntimeException("请先登录");
        }
        //userId存储到ThreadLocal(线程的局部变量,可以满足在一个线程内的全局共享)
        UserThreadLocal.set(userId);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        UserThreadLocal.remove();
    }
}
