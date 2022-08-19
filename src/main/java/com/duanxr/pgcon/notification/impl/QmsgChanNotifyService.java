package com.duanxr.pgcon.notification.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import java.util.Map;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/8/9
 */
@Slf4j
public class QmsgChanNotifyService extends HttpNotifyService {

  private static final String URL = "https://qmsg.zendee.cn:443/send/";
  @Setter
  private String token;

  @Setter
  private String qq;


  @Override
  @SneakyThrows
  public boolean push(String title, String message) {
    Map<String, String> params = Map.of("qq", qq, "msg", title + "|" + message);
    String url = URL + token + "/";
    String response = super.postForm(url, params);
    if (Strings.isNullOrEmpty(response)) {
      return false;
    }
    JSONObject responseJson = JSONObject.parseObject(response);
    return responseJson != null && responseJson.getBooleanValue("success");
  }

}
