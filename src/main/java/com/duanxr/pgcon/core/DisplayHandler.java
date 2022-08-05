package com.duanxr.pgcon.core;

import com.duanxr.pgcon.config.GuiConfig;
import com.duanxr.pgcon.config.InputConfig;
import com.duanxr.pgcon.core.FrameManager.CachedFrame;
import com.duanxr.pgcon.gui.display.DrawEvent;
import com.duanxr.pgcon.gui.display.api.Drawable;
import com.duanxr.pgcon.input.impl.CameraImageInput;
import com.duanxr.pgcon.input.impl.StaticImageInput;
import com.duanxr.pgcon.util.ImageResizeUtil;
import com.google.common.base.Strings;
import com.google.common.eventbus.Subscribe;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import javax.annotation.PreDestroy;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Duanran 2019/12/21
 */
@Slf4j
@Component
public class DisplayHandler {

  private static final StaticImageInput DEFAULT_IMAGE_INPUT = new StaticImageInput(
      "/img/no_input.bmp");
  private static final int NOTIFY_CANVAS_DELAY = 200;
  private final Map<String, Drawable> canvasDrawables;
  private final FrameManager frameManager;
  private final AtomicBoolean frozenScreen;
  private final GuiConfig guiConfig;
  private final ScheduledExecutorService scheduledExecutorService;
  private Consumer<BufferedImage> canvas;
  @Getter
  private volatile CameraImageInput imageInput;
  private volatile Consumer<BufferedImage> screen;

  @Autowired
  public DisplayHandler(InputConfig inputConfig, GuiConfig guiConfig,
      ExecutorService executorService, AtomicBoolean frozenScreen, FrameManager frameManager) {
    this.guiConfig = guiConfig;
    this.frozenScreen = frozenScreen;
    this.frameManager = frameManager;
    this.canvasDrawables = new HashMap<>();
    this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    executorService.execute(DaemonTask.of("LOAD_FRAME", this::readFrame));
    executorService.execute(DaemonTask.of("RENDER_SCREEN", this::renderScreen));
    executorService.execute(DaemonTask.of("RENDER_CANVAS", this::renderCanvas));
  }

  /**
   * v1 readFrame cost 8-30 ms, v2 readFrame cost 8-15 ms
   */
  private synchronized void readFrame() {
    if (screen != null && imageInput != null) {
      BufferedImage read = imageInput.read();
      if (read != null) {
        this.frameManager.setFrame(read);
      }
    }
  }

  private void renderScreen() {
    if (screen != null && imageInput != null) {
      CachedFrame cachedFrame = frameManager.getNewFrame();
      if (cachedFrame != null) {
        renderScreen(cachedFrame.getImage());
      }
    }
  }

  @SneakyThrows
  private void renderCanvas() {
    synchronized (canvasDrawables) {
      canvasDrawables.wait();
      if (canvas != null && !frozenScreen.get()) {
        BufferedImage transparentCanvas = createTransparentCanvas();
        Graphics graphics = transparentCanvas.getGraphics();
        for (Iterator<Entry<String, Drawable>> iterator = canvasDrawables.entrySet().iterator();
            iterator.hasNext(); ) {
          Drawable drawable = iterator.next().getValue();
          if (drawable.isExpired()) {
            iterator.remove();
          } else {
            drawable.draw(graphics);
          }
        }
        canvas.accept(transparentCanvas);
      }
    }
  }

  /**
   * v1 resize cost 95 ms v2 resize cost 6 ms, the fastest so far,
   */
  @Subscribe
  public void renderScreen(BufferedImage frame) {
    if (screen != null && !frozenScreen.get()) {
      BufferedImage resize = ImageResizeUtil.resizeV2(frame, guiConfig.getWidth(),
          guiConfig.getHeight());
      screen.accept(resize);
    }
  }

  private BufferedImage createTransparentCanvas() {
    BufferedImage transparentCanvas = new BufferedImage(
        guiConfig.getWidth(), guiConfig.getHeight(),
        BufferedImage.TYPE_INT_ARGB);
    int[] outputImagePixelData = ((DataBufferInt) transparentCanvas.getRaster()
        .getDataBuffer()).getData();
    Arrays.fill(outputImagePixelData, 0);
    return transparentCanvas;
  }

  public void registerScreen(Consumer<BufferedImage> screen) {
    this.screen = screen;
    repaintDefault();
  }

  private void repaintDefault() {
    screen.accept(DEFAULT_IMAGE_INPUT.read());
  }

  public void registerCanvas(Consumer<BufferedImage> canvas) {
    this.canvas = canvas;
  }

  public void draw(DrawEvent drawEvent) {
    if (!Strings.isNullOrEmpty(drawEvent.getKey())) {
      boolean drawNew = drawEvent.getDrawable() != null;
      synchronized (canvasDrawables) {
        if (drawNew) {
          canvasDrawables.put(drawEvent.getKey(), drawEvent.getDrawable());
        } else {
          canvasDrawables.remove(drawEvent.getKey());
        }
        notifyCanvas();
      }
      if (drawNew && drawEvent.getDrawable().getDuration() > 0) {
        notifyCanvasLater(drawEvent.getDrawable().getDuration());
      }
    }
  }

  public void notifyCanvas() {
    synchronized (canvasDrawables) {
      canvasDrawables.notifyAll();
    }
  }

  private void notifyCanvasLater(long duration) {
    try {
      scheduledExecutorService.schedule(this::notifyCanvas, duration + NOTIFY_CANVAS_DELAY,
          TimeUnit.MILLISECONDS);
    } catch (Exception ignored) {
    }
  }

  public void setImageInput(CameraImageInput imageInput) {
    close();
    this.imageInput = imageInput;
  }

  @PreDestroy
  public synchronized void close() {
    if (imageInput != null) {
      imageInput.close();
      imageInput = null;
    }
  }

}
