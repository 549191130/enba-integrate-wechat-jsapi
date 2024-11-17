package com.enba.intagrate.jsapi.controller;

import com.alibaba.fastjson.JSON;
import com.enba.intagrate.jsapi.properties.EnbaJsapiProperties;
import com.enba.intagrate.jsapi.properties.EnbaMpProperties;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.exception.ValidationException;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.partnerpayments.jsapi.model.Transaction;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.payments.jsapi.model.Amount;
import com.wechat.pay.java.service.payments.jsapi.model.Payer;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: enba
 * @description: 恩爸整合JSAPI
 */
@RequestMapping("/enba-jsapi")
@RestController
@Slf4j
public class EnbaJsapiController {

  private static final Logger logger = org.slf4j.LoggerFactory.getLogger(EnbaJsapiController.class);

  private final JsapiService service;
  private final EnbaMpProperties properties;
  private final EnbaJsapiProperties jsapiProperties;

  public EnbaJsapiController(
      JsapiService service, EnbaMpProperties properties, EnbaJsapiProperties jsapiProperties) {
    this.service = service;
    this.properties = properties;
    this.jsapiProperties = jsapiProperties;
  }

  @GetMapping("/index")
  public String index() {
    return "index";
  }

  /**
   * JSAPI支付下单
   *
   * @return PrepayResponse
   */
  @GetMapping("/prepay")
  public PrepayResponse prepay() {
    PrepayRequest request = new PrepayRequest();
    // 调用request.setXxx(val)设置所需参数，具体参数可见Request定义,这里只维护必填参数，其他参数见文档描述

    // 【公众号ID】 公众号ID
    request.setAppid(properties.getAppId());
    // 【商户号】 商户号
    request.setMchid(jsapiProperties.getMerchantId());
    // 【商品描述】 商品描述
    request.setDescription("商品描述");
    // 【商户订单号】 商户系统内部订单号，只能是数字、大小写字母_-*且在同一个商户号下唯一
    request.setOutTradeNo("1217752501201407033233368018");
    // 【通知地址】 异步接收微信支付结果通知的回调地址，通知URL必须为外网可访问的URL，不能携带参数。 公网域名必须为HTTPS
    request.setNotifyUrl("https://www.weixin.qq.com/wxpay/pay.php");

    // 订单金额信息
    Amount amount = new Amount();
    // 订单总金额，单位为分
    amount.setTotal(1);
    request.setAmount(amount);

    // 支付者信息
    Payer payer = new Payer();
    // 用户的openid
    payer.setOpenid("oUpF8uMuAJO_M2pxb1Q9zNjWeS6o");
    request.setPayer(payer);

    // 调用接口
    return service.prepay(request);
  }

  // 定义回调接口
  @GetMapping("/pay-callback")
  public BodyBuilder callback(RequestParam requestParam) {

    // 如果已经初始化了 RSAAutoCertificateConfig，可直接使用
    // 没有的话，则构造一个
    NotificationConfig config =
        new RSAAutoCertificateConfig.Builder()
            .merchantId(jsapiProperties.getMerchantId())
            .privateKeyFromPath(jsapiProperties.getPrivateKeyPath())
            .merchantSerialNumber(jsapiProperties.getMerchantSerialNumber())
            .apiV3Key(jsapiProperties.getApiV3Key())
            .build();

    // 初始化 NotificationParser
    NotificationParser parser = new NotificationParser(config);
    try {
      // 以支付通知回调为例，验签、解密并转换成 Transaction
      Transaction transaction = parser.parse(requestParam, Transaction.class);

      logger.info("transaction: {}", JSON.toJSONString(transaction));
    } catch (ValidationException e) {
      // 签名验证失败，返回 401 UNAUTHORIZED 状态码
      logger.error("sign verification failed", e);

      return ResponseEntity.status(HttpStatus.UNAUTHORIZED);
    }

    // 如果处理失败，应返回 4xx/5xx 的状态码，例如 500 INTERNAL_SERVER_ERROR
    //    if (transaction.) {
    //      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
    //    }

    // 处理成功，返回 200 OK 状态码
    return ResponseEntity.status(HttpStatus.OK);
  }
}
