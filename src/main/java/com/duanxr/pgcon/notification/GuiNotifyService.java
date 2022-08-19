package com.duanxr.pgcon.notification;

import com.duanxr.pgcon.notification.impl.PushPlusNotifyService;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/8/19
 */
@Component
public class GuiNotifyService implements NotifyService {
  private PushPlusNotifyService pushPlusNotifyService;

  public GuiNotifyService() {
    this.pushPlusNotifyService = new PushPlusNotifyService();
    pushPlusNotifyService.setToken("222d6226accc4f568f3e1bf508617d12");
  }

  @Override
  public boolean push(String title, String message) {
    return pushPlusNotifyService.push(title, message);
  }
}
