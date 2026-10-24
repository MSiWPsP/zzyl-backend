package com.zzyl.nursing.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zzyl.nursing.service.WeChatService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeChatServiceImpl implements WeChatService {

    // 登录
    private static final String REQUEST_URL = "https://api.weixin.qq.com/sns/jscode2session?grant_type=authorization_code";

    // 获取token
    private static final String TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential";

    // 获取手机号
    private static final String PHONE_REQUEST_URL = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=";

    @Override
    public String getOpenId(String code) {
        Map<String, Object> paramMap = getAppConfig();
        paramMap.put("js_code",code);
        String jsonObjectStr = HttpUtil.get(REQUEST_URL, paramMap);
        JSONObject jsonObject = JSONUtil.parseObj(jsonObjectStr);
        if (jsonObject.getInt("errcode") != null){
            throw new RuntimeException(jsonObject.getStr("errmsg"));
        }
        return jsonObject.getStr("openid");
    }

    @Value("${wechat.appid}")
    private String appid;

    @Value("${wechat.secret}")
    private String secret;


    private Map<String, Object> getAppConfig() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("appid",appid);
        map.put("secret",secret);
        return map;
    }

    @Override
    public String getPhone(String detailCode) {
        String token = getToken();
        String url = PHONE_REQUEST_URL + token;
        HashMap<String, Object> map = new HashMap<>();
        map.put("code",detailCode);
        String jsonObjectStr = HttpUtil.post(url, JSONUtil.toJsonStr(map));
        JSONObject jsonObject = JSONUtil.parseObj(jsonObjectStr);
        if (jsonObject.getInt("errcode") != 0){
            throw new RuntimeException(jsonObject.getStr("errmsg"));
        }
        return jsonObject.getJSONObject("phone_info").getStr("phoneNumber");
    }

    private String getToken() {
        Map<String, Object> map = getAppConfig();
        String jsonObjectStr = HttpUtil.get(TOKEN_URL, map);
        JSONObject jsonObject = JSONUtil.parseObj(jsonObjectStr);
        if (jsonObject.getInt("errcode") != null){
            throw new RuntimeException(jsonObject.getStr("errmsg"));
        }
        return jsonObject.getStr("access_token");
    }
}
