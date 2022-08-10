package com.duanxr.pgcon.test;

import com.duanxr.pgcon.notification.impl.PushPlusNotifyService;
import com.duanxr.pgcon.notification.impl.PushPlusNotifyService.Channel;
import com.duanxr.pgcon.notification.impl.QmsgChanNotifyService;
import com.duanxr.pgcon.notification.impl.ServerChanNotifyService;
import com.duanxr.pgcon.notification.impl.TgChanNotifyService;
import lombok.SneakyThrows;

/**
 * @author 段然 2022/8/10
 */
public class NotifyServiceTest {

  @SneakyThrows
  public static void main(String[] args) {
    testPushPlus();
    testTgChan();
    testQmsgChan();
    testServerChan();
  }

  private static void testServerChan() {
    ServerChanNotifyService service = new ServerChanNotifyService();
    service.setToken("????");
    System.out.println(service.push("Server酱测试消息", "Server酱测试消息Body"));
  }

  private static void testQmsgChan() {
    QmsgChanNotifyService service = new QmsgChanNotifyService();
    service.setToken("????");
    service.setQq("337845818");
    System.out.println(service.push("QMSG测试消息", "QMSG测试消息Body"));
  }

  private static void testTgChan() {
    TgChanNotifyService service = new TgChanNotifyService();
    service.setToken("????");
    System.out.println(service.push("TG测试消息", "TG测试消息Body"));
  }

  @SneakyThrows
  private static void testPushPlus() {
    PushPlusNotifyService pushPlusNotifyService = new PushPlusNotifyService();
    pushPlusNotifyService.setChannel(Channel.wechat);
    pushPlusNotifyService.setToken("????");
    System.err.println(pushPlusNotifyService.push("微信测试消息", "微信测试消息Body"));
    Thread.sleep(1000);
    pushPlusNotifyService.setChannel(Channel.mail);
    System.err.println(pushPlusNotifyService.push("邮箱测试消息", "邮箱测试消息Body"));
    Thread.sleep(1000);
  }
}
