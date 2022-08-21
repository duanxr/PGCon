package com.duanxr.pgcon.output;

import static com.duanxr.pgcon.config.ConstantConfig.AUTO_RELEASE_DELAY;

import com.duanxr.pgcon.component.DaemonTask;
import com.duanxr.pgcon.config.OutputConfig;
import com.duanxr.pgcon.gui.display.DisplayService;
import com.duanxr.pgcon.output.action.ButtonAction;
import com.duanxr.pgcon.output.action.StickAction;
import com.duanxr.pgcon.output.api.Protocol;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;
import lombok.Getter;
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
public class ControllerService {
  private static final ButtonAction[] BUTTON_ACTIONS = ButtonAction.values();
  private final long[] buttonExpired;
  private final DisplayService displayService; //TODO DEBUG WITH CONTROLLER IN THE CANVAS
  private final ButtonAction[] hatDirections;
  private final OutputConfig outputConfig;
  private final StickAction[] stickDirections;
  private final long[] stickExpired;
  @Setter
  @Getter
  private volatile Protocol protocol;
  @Autowired
  public ControllerService(OutputConfig outputConfig, ExecutorService executorService,
      DisplayService displayService) {
    this.outputConfig = outputConfig;
    this.displayService = displayService;
    this.buttonExpired = new long[ButtonAction.values().length];
    this.stickExpired = new long[2];
    this.hatDirections = new ButtonAction[]{null};
    this.stickDirections = new StickAction[]{StickAction.L_CENTER, StickAction.R_CENTER};
    executorService.execute(DaemonTask.of("CHECK_CONTROLLER", this::autoRelease));
  }

  @SneakyThrows
  private void autoRelease() {
    for (int i = 0, length = stickExpired.length; i < length; i++) {
      if(stickExpired[i] != 0 &&  System.currentTimeMillis() >= stickExpired[i]) {
        release(i==0?StickAction.L_CENTER:StickAction.R_CENTER);
      }
    }
    for (int i = 0, length = buttonExpired.length; i < length; i++) {
      if(buttonExpired[i] != 0 &&  System.currentTimeMillis() >= buttonExpired[i]) {
        release(BUTTON_ACTIONS[i]);
      }
    }
    TimeUnit.MILLISECONDS.sleep(AUTO_RELEASE_DELAY);
  }

  public synchronized void release(StickAction action) {
    int index = getExpireTimeIndex(action);
    long duration = stickExpired[index];
    if (duration != 0) {
      stickExpired[index] = 0;
      if (protocol != null) {
        protocol.set(getReleaseAction(action));
      }
      stickDirections[index] = getReleaseAction(action);
    }
  }

  public synchronized void release(ButtonAction action) {
    int index = getExpireTimeIndex(action);
    if (buttonExpired[index] != 0) {
      buttonExpired[index] = 0;
      if (protocol != null) {
        protocol.release(action);
      }
      if (action.isHat()) {
        hatDirections[0] = null;
      }
    }
  }

  private int getExpireTimeIndex(StickAction action) {
    return action.isLeft() ? 0 : 1;
  }

  private StickAction getReleaseAction(StickAction action) {
    return action.isLeft() ? StickAction.L_CENTER : StickAction.R_CENTER;
  }

  private int getExpireTimeIndex(ButtonAction action) {
    return action.isHat() ? ButtonAction.D_TOP.ordinal() : action.ordinal();
  }

  private ButtonAction getReleaseAction(ButtonAction action) {
    return action.isHat() ? ButtonAction.D_TOP : action;
  }

  public synchronized void press(ButtonAction action) {
    hold(action, getDefaultPressTime());
  }

  public synchronized void hold(ButtonAction action, int duration) {
    long expiredTime = System.currentTimeMillis() + duration;
    checkAndHold(action, expiredTime);
  }

  private int getDefaultPressTime() {
    return outputConfig.getPressTime();
  }

  public synchronized void checkAndHold(ButtonAction action, long expiredTime) {
    int index = getExpireTimeIndex(action);
    if (buttonExpired[index] == 0) {
      buttonExpired[index] = expiredTime;
      if (protocol != null) {
        protocol.hold(action);
      }
      if (action.isHat()) {
        hatDirections[0] = action;
      }
    } else {
      if (action.isHat()) {
        if (hatDirections[0] == action) {
          if (buttonExpired[index] < expiredTime) {
            buttonExpired[index] = expiredTime;
          }
        } else {
          buttonExpired[index] = expiredTime;
          if (protocol != null) {
            protocol.release(hatDirections[0]);
            protocol.hold(action);
          }
          hatDirections[0] = action;
        }
      } else {
        if (buttonExpired[index] < expiredTime) {
          buttonExpired[index] = expiredTime;
        }
      }
    }
  }

  public synchronized void hold(ButtonAction action) {
    long expiredTime = Long.MAX_VALUE;
    checkAndHold(action, expiredTime);
  }

  public synchronized void press(StickAction action) {
    hold(action, getDefaultPressTime());
  }

  public void hold(StickAction action, int duration) {
    long expiredTime = System.currentTimeMillis() + duration;
    checkAndHold(action, expiredTime);
  }

  private synchronized void checkAndHold(StickAction action, long expiredTime) {
    int index = getExpireTimeIndex(action);
    if (stickExpired[index] == 0) {
      stickExpired[index] = expiredTime;
      if (protocol != null) {
        protocol.set(action);
      }
    } else {
      if (stickDirections[index] == action) {
        if (stickExpired[index] < expiredTime) {
          stickExpired[index] = expiredTime;
        }
      } else {
        stickExpired[index] = expiredTime;
        if (protocol != null) {
          protocol.set(action);
        }
      }
    }
    stickDirections[index] = action;
  }

  public void hold(StickAction action) {
    long expiredTime = Long.MAX_VALUE;
    checkAndHold(action, expiredTime);
  }

  @PreDestroy
  @SneakyThrows
  public void clear() {
    if (protocol != null) {
      protocol.clear();
      protocol = null;
      Arrays.fill(stickExpired,0);
      Arrays.fill(buttonExpired,0);
      hatDirections[0]=null;
      stickDirections[0]= StickAction.L_CENTER;
      stickDirections[1]= StickAction.R_CENTER;
    }
  }


}
