package com.enba.intagrate.jsapi.config;

import com.enba.intagrate.jsapi.properties.EnbaJsapiProperties;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsapiServiceConfig {

  @Bean
  public JsapiService jsapiService(EnbaJsapiProperties properties) {
    Config config =
        new RSAAutoCertificateConfig.Builder()
            .merchantId(properties.getMerchantId())
            // 使用 com.wechat.pay.java.core.util 中的函数从本地文件中加载商户私钥，商户私钥会用来生成请求的签名
            .privateKeyFromPath(properties.getPrivateKeyPath())
            .merchantSerialNumber(properties.getMerchantSerialNumber())
            .apiV3Key(properties.getApiV3Key())
            .build();

    // 初始化服务
    return new JsapiService.Builder().config(config).build();
  }
}
