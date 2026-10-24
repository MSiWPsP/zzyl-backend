package com.zzyl.common;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import cn.hutool.json.JSONUtil;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;


public class HttpTest {

    @Test
    void testGet(){
        String s = HttpUtil.get("https://www.baidu.com");
        System.out.println(s);
    }

    @Test
    void testGet2(){
        HttpRequest request = HttpUtil.createRequest(Method.GET, "http://localhost:8080/nursing/project/list");
        Map<String, Object> map = new HashMap<>();
        map.put("pageNum",1);
        map.put("pageSize",5);
        request.form(map);
        request.header("authorization","Bearer eyJhbGciOiJIUzUxMiJ9.eyJsb2dpbl91c2VyX2tleSI6IjdkZDk0NjA5LTMyZDAtNDFiMS05YTU2LTRkM2UzNzhhOGQ0NSJ9.XY6OsH3GAPGcvZ4W6ruwrIhj8GlTfboi_jQUCt-p7fabNipp3jRt8DiNDwQzPnwSHujH2mvoqEJ45-sd3rrmzg");
        HttpResponse httpResponse = request.execute();
        if (httpResponse.isOk()){
            System.out.println(httpResponse.body());
        }
    }

    @Test
    void testPost(){
        HttpRequest request = HttpUtil.createRequest(Method.POST, "http://localhost:8080/nursing/project");
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", "护理项目测试");
        paramMap.put("orderNo", 1);
        paramMap.put("unit", "次");
        paramMap.put("price", 10.00);
        paramMap.put("image", "https://yjy-slwl-oss.oss-cn-hangzhou.aliyuncs.com/ae7cf766-fb7b-49ff-a73c-c86c25f280e1.png");
        paramMap.put("nursingRequirement", "无特殊要求");
        paramMap.put("status", 1);
        request.header("authorization","Bearer eyJhbGciOiJIUzUxMiJ9.eyJsb2dpbl91c2VyX2tleSI6IjdkZDk0NjA5LTMyZDAtNDFiMS05YTU2LTRkM2UzNzhhOGQ0NSJ9.XY6OsH3GAPGcvZ4W6ruwrIhj8GlTfboi_jQUCt-p7fabNipp3jRt8DiNDwQzPnwSHujH2mvoqEJ45-sd3rrmzg");
        request.body(JSONUtil.toJsonStr(paramMap));
        HttpResponse response = request.execute();
        if (response.isOk()){
            System.out.println(response.body());
        }
    }

}
