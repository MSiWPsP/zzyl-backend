package com.zzyl.framework.interceptor;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.zzyl.common.core.domain.model.LoginUser;
import com.zzyl.common.utils.SecurityUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Autowired
    private HttpServletRequest request;

    public boolean judgeMember(){
        String uri = request.getRequestURI();
        if (uri.startsWith("/member")){
            return true;
        }
        return false;
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        if (!judgeMember()){
            this.strictInsertFill(metaObject,"createBy", String.class,loginUser());
        }
        this.strictInsertFill(metaObject,"createTime", Date.class,new Date());
    }

    //获取当前登录者id
    private String loginUser() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null){
            return loginUser.getUserId().toString();
        }
        return null;
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if (!judgeMember()){
            this.setFieldValByName("updateBy",loginUser(),metaObject);
        }
        this.setFieldValByName("updateTime",new Date(),metaObject);
    }
}
