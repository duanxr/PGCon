package com.duanxr.rhm.core.handler;

import static com.duanxr.rhm.config.ConstantConfig.OUTPUT_HOLD_TIME;
import static com.duanxr.rhm.config.ConstantConfig.OUTPUT_PRESS_CHECK_INTERVAL;

import com.duanxr.rhm.core.handler.action.ButtonAction;
import com.duanxr.rhm.core.handler.action.PressAction;
import com.duanxr.rhm.core.handler.action.StickAction;
import com.duanxr.rhm.core.handler.action.StickSimpleAction;
import com.duanxr.rhm.io.output.ControllerOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Duanran 2019/12/17
 */
@Slf4j
//@Service
@Deprecated
public class ActionHandlerv1 {

  private final Map<ButtonAction, Long> pressCache;
  private Executor pressReleaseThread;
  private List<ButtonAction> releaseList;
  @Setter
  private ControllerOutput controllerOutput;

  public ActionHandlerv1() {
    this.pressCache = new HashMap<>();
    this.releaseList = new ArrayList<>();
    this.pressReleaseThread = Executors.newSingleThreadExecutor();
    this.pressReleaseThread.execute(this::autoRelease);
  }

  @SneakyThrows
  private void autoRelease() {
    while (true) {
      try {
        synchronized (pressCache) {
          for (Entry<ButtonAction, Long> entry : pressCache.entrySet()) {
            if (System.currentTimeMillis() - entry.getValue() > OUTPUT_HOLD_TIME) {
              releaseList.add(entry.getKey());
            }
          }
          for (ButtonAction buttonAction : releaseList) {
            send(buttonAction.getUpCommand());
            pressCache.remove(buttonAction);
          }
          releaseList.clear();
        }
      } catch (Exception e) {
        log.error("autoRelease exception.", e);
      }
      TimeUnit.MILLISECONDS.sleep(OUTPUT_PRESS_CHECK_INTERVAL);
    }
  }

  @SneakyThrows
  public void sendAction(ButtonAction buttonType, PressAction pressAction) {
    switch (pressAction) {
      case DOWN:
        synchronized (pressCache) {
          if (pressCache.containsKey(buttonType)) {
            send(buttonType.getUpCommand());
            pressCache.remove(buttonType);
          }
          send(buttonType.getDownCommand());
        }
        break;
      case UP:
        synchronized (pressCache) {
          pressCache.remove(buttonType);
        }
        send(buttonType.getUpCommand());
        break;
      default:
        synchronized (pressCache) {
          if (pressCache.containsKey(buttonType)) {
            send(buttonType.getUpCommand());
          }
          pressCache.put(buttonType, System.currentTimeMillis());
          send(buttonType.getDownCommand());
        }
        break;
    }
  }

  public void sendAction(StickSimpleAction stickSimpleAction) {
    send(stickSimpleAction.getActionX().getCommand());
    send(stickSimpleAction.getActionY().getCommand());
  }

  public void sendAction(StickAction stickAction) {
    send(stickAction.getCommand());
  }

  @SneakyThrows
  private synchronized void send(int command) {
    if (controllerOutput != null) {
      controllerOutput.output(command);
    }
  }

  public void clear() {
    send(49);
  }
}
