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
public class ServerChanNotifyService extends HttpNotifyService {

  private static final String URL = "https://sctapi.ftqq.com/%s.send?";
  @Setter
  private String token;


  @Override
  @SneakyThrows
  public boolean push(String title, String message) {
    String url = URL.formatted(token);
    url += "title=" + URL_CODEC.encode(title) + "&desp=" + URL_CODEC.encode(message);
    String response = super.get(url);
    if (Strings.isNullOrEmpty(response)) {
      return false;
    }
    JSONObject responseJson = JSONObject.parseObject(response);
    return responseJson != null && responseJson.getIntValue("code") == 0;
  }

}
