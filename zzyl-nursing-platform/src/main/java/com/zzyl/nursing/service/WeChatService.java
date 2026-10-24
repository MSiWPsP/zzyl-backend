package com.zzyl.nursing.service;

public interface WeChatService {

    /**
     * 获取用户的openId
     * @param code
     * @return
     */
    public String getOpenId(String code);

    /**
     * 获取用户的手机号
     * @param detailCode
     * @return
     */
    public String getPhone(String detailCode);

}
