package com.duanxr.pgcon.output;

import com.duanxr.pgcon.config.OutputConfig;
import com.duanxr.pgcon.output.action.ButtonAction;
import com.duanxr.pgcon.output.action.StickAction;
import com.duanxr.pgcon.output.api.Protocol;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.concurrent.ExecutorService;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/7/25
 */
@Slf4j
@Component
public class Controller {

  private final OutputConfig outputConfig;
  private final BiMap<StickAction, Integer> stickMap;
  private final BiMap<ButtonAction, Integer> buttonMap;

  private final BiMap<Integer, StickAction> reversedStickMap;
  private final BiMap<Integer, ButtonAction> reversedButtonMap;

  private final long[] releaseTime;
  @Setter
  private Protocol protocol;

  @Autowired
  public Controller(OutputConfig outputConfig, ExecutorService executors) {
    this.outputConfig = outputConfig;
    stickMap = HashBiMap.create();
    buttonMap = HashBiMap.create();
    int index = 0;
    for (StickAction action : StickAction.values()) {
      stickMap.put(action, index++);
    }
    for (ButtonAction action : ButtonAction.values()) {
      buttonMap.put(action, index++);
    }
    reversedStickMap = stickMap.inverse();
    reversedButtonMap = buttonMap.inverse();
    releaseTime = new long[index];
    executors.execute(() -> {
      try {
        while (!Thread.currentThread().isInterrupted()) {
          for (int i = 0, releaseTimeLength = releaseTime.length; i < releaseTimeLength; i++) {
            long t = releaseTime[i];
            if (t != 0 && t < System.currentTimeMillis()) {
              release(i);
            }
          }
          Thread.sleep(5);
        }
      } catch (Exception e) {
        log.error("", e);
      }
    });
  }

  private synchronized void release(int index) {
    if (index >= stickMap.size()) {
      release(reversedButtonMap.get(index));
    } else {
      release(reversedStickMap.get(index));
    }
  }

  public synchronized void press(ButtonAction action) {
    hold(action, getDefaultPressTime());
  }

  public synchronized void hold(ButtonAction action, int time) {
    if (readAndSetMax(releaseTime, buttonMap.get(action),
        System.currentTimeMillis() + time)) {
      protocol.hold(action);
    }
  }

  public synchronized void hold(ButtonAction action) {
    if (readAndSetMax(releaseTime, buttonMap.get(action),
        Long.MAX_VALUE)) {
      protocol.hold(action);
    }
  }

  public synchronized void release(ButtonAction action) {
    if (setToZero(releaseTime, buttonMap.get(action))) {
      protocol.release(action);
    }
  }

  public synchronized void press(StickAction action) {
    hold(action, getDefaultPressTime());
  }

  public synchronized void hold(StickAction action, int time) {
    if (readAndSetMax(releaseTime, stickMap.get(action),
        System.currentTimeMillis() + time)) {
      protocol.set(action);
    }
  }

  public synchronized void hold(StickAction action) {
    if (readAndSetMax(releaseTime, stickMap.get(action),
        Long.MAX_VALUE)) {
      protocol.set(action);
    }
  }

  public synchronized void release(StickAction action) {
    if (setToZero(releaseTime, stickMap.get(action))) {
      protocol.set(getResetAction(action));
    }
  }

  private StickAction getResetAction(StickAction action) {
    return action.isLeft() ? StickAction.L_CENTER : StickAction.R_CENTER;
  }

  private boolean readAndSetMax(long[] array, int index, long value) {
    long old = array[index];
    if (value > old) {
      array[index] = value;
      return old == 0L;
    }
    return false;
  }

  private boolean setToZero(long[] array, int index) {
    long old = array[index];
    if (old != 0L) {
      array[index] = 0L;
      return true;
    }
    return false;
  }

  private int getDefaultPressTime() {
    return outputConfig.getPressTime();
  }

  @SneakyThrows
  public void clear() {
    if (protocol != null) {
      protocol.clear();
      protocol = null;
    }
  }

}
