package com.zzyl;

import com.huaweicloud.sdk.iotda.v5.IoTDAClient;
import com.zzyl.framework.config.IotClientConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class IoTClientTest {

    @Autowired
    private IoTDAClient ioTDAClient;

    @Test
    public void test(){
        System.out.println(ioTDAClient);
    }

}
