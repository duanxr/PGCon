package com.duanxr.pgcon.notification.impl;

import com.duanxr.pgcon.notification.NotifyService;
import java.util.Map;
import lombok.SneakyThrows;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.codec.net.URLCodec;

/**
 * @author 段然 2022/8/10
 */
public abstract class HttpNotifyService implements NotifyService {

  protected static OkHttpClient client = new OkHttpClient().newBuilder().build();
  protected static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json");
  protected static final MediaType FORM_MEDIA_TYPE = MediaType.parse(
      "application/x-www-form-urlencoded");

  protected static final URLCodec URL_CODEC = new URLCodec();

  @SneakyThrows
  protected String postRaw(String url, String body) {
    RequestBody requestBody = RequestBody.create(body, JSON_MEDIA_TYPE);
    Request request = new Request.Builder()
        .url(url)
        .method("POST", requestBody)
        .build();
    try (Response response = client.newCall(request).execute()) {
      ResponseBody responseBody = response.body();
      if (responseBody != null) {
        return responseBody.string();
      }
    }
    return null;
  }

  @SneakyThrows
  protected String postForm(String url, Map<String, String> body) {
    String formBody = buildFormBody(body);
    RequestBody requestBody = RequestBody.create(formBody, FORM_MEDIA_TYPE);
    Request request = new Request.Builder()
        .url(url)
        .method("POST", requestBody)
        .build();
    try (Response response = client.newCall(request).execute()) {
      ResponseBody responseBody = response.body();
      if (responseBody != null) {
        return responseBody.string();
      }
    }
    return null;
  }

  @SneakyThrows
  private String buildFormBody(Map<String, String> body) {
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, String> entry : body.entrySet()) {
      sb.append(entry.getKey()).append("=").append(URL_CODEC.encode(entry.getValue())).append("&");
    }
    sb.deleteCharAt(sb.length() - 1);
    return sb.toString();
  }


  @SneakyThrows
  protected String get(String url) {
    Request request = new Request.Builder()
        .url(url)
        .method("GET", null)
        .build();
    try (Response response = client.newCall(request).execute()) {
      ResponseBody responseBody = response.body();
      if (responseBody != null) {
        return responseBody.string();
      }
    }
    return null;
  }
}
