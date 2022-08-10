package com.duanxr.pgcon.notification.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/8/9
 */
@Slf4j
@Component
public class PushPlusNotifyService extends HttpNotifyService {

  private static final String URL = "http://www.pushplus.plus/send";

  @Setter
  private Channel channel;
  @Setter
  private String token;

  public PushPlusNotifyService() {
    this.channel = Channel.wechat;
  }

  @Override
  @SneakyThrows
  public boolean push(String title, String message) {
    JSONObject body = new JSONObject();
    body.put("title", title);
    body.put("content", message);
    body.put("channel", channel);
    body.put("token", token);
    String response = super.postRaw(URL, body.toJSONString());
    if (Strings.isNullOrEmpty(response)) {
      return false;
    }
    JSONObject responseJson = JSONObject.parseObject(response);
    return responseJson != null && responseJson.getIntValue("code") == 200;
  }

  public enum Channel {
    wechat, mail
  }
}
