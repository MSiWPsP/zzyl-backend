package com.zzyl;

import com.zzyl.nursing.service.WeChatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class WeChatTest {

    @Autowired
    private WeChatService weChatService;

    @Test
    public void testGetOpenId(){
        String openId = weChatService.getOpenId("0a1IpHml2DINEi4s8Iml2YqXUq4IpHmg");
        System.out.println(openId);
    }


    @Test
    public void testPhone(){
        String phone = weChatService.getPhone("d7729a6112b9b94f5a8212a377df43a046cabe251b0b9e7dd569631699565bab");
        System.out.println(phone);
    }

}
