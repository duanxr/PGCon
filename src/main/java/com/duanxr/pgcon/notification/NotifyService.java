package com.duanxr.pgcon.notification;

/**
 * @author 段然 2022/8/9
 */
public interface NotifyService {

  boolean push(String title, String message);
}
