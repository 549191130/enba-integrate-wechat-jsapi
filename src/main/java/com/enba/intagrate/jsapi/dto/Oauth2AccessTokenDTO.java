package com.enba.intagrate.jsapi.dto;

import lombok.Data;

@Data
public class Oauth2AccessTokenDTO {

  /** 网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同 */
  private String access_token;

  /** access_token接口调用凭证超时时间，单位（秒） */
  private Long expires_in;

  /** 用户唯一标识，请注意，在未关注公众号时，用户访问公众号的网页，也会产生一个用户和公众号唯一的OpenID */
  private String openid;

  /** 用户刷新access_token */
  private String refresh_token;

  /** 用户授权的作用域，使用逗号（,）分隔 */
  private String scope;

  /** 用户统一标识（针对一个微信开放平台账号下的应用，同一用户的 unionid 是唯一的），只有当scope为"snsapi_userinfo"时返回 */
  private String unionid;
}
