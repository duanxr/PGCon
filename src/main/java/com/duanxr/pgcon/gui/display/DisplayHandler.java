package com.duanxr.pgcon.gui.display;

import com.duanxr.pgcon.config.GuiConfig;
import com.duanxr.pgcon.config.InputConfig;
import com.duanxr.pgcon.gui.display.canvas.DrawEvent;
import com.duanxr.pgcon.gui.display.canvas.api.Drawable;
import com.duanxr.pgcon.input.component.FrameManager;
import com.duanxr.pgcon.input.component.FrameManager.CachedFrame;
import com.duanxr.pgcon.input.impl.CameraImageInput;
import com.duanxr.pgcon.input.impl.StaticImageInput;
import com.duanxr.pgcon.util.ImageResizeUtil;
import com.google.common.base.Strings;
import com.google.common.eventbus.Subscribe;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import lombok.Getter;
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
  private final Map<String, Drawable> drawableHashMap;
  private final long frameInterval;
  private final FrameManager frameManager;
  private final AtomicBoolean frozenScreen;
  private final GuiConfig guiConfig;
  private final long renderInterval;
  private Consumer<BufferedImage> canvas;
  @Getter
  private volatile CameraImageInput imageInput;
  private volatile Consumer<BufferedImage> screen;

  @Autowired
  public DisplayHandler(InputConfig inputConfig, GuiConfig guiConfig, ExecutorService executorService,
      AtomicBoolean frozenScreen, FrameManager frameManager) {
    this.guiConfig = guiConfig;
    this.frozenScreen = frozenScreen;
    this.frameManager = frameManager;
    this.drawableHashMap = new ConcurrentHashMap<>();
    this.frameInterval = inputConfig.getFrameInterval();
    this.renderInterval = inputConfig.getRenderInterval();
    executorService.execute(this::readFrame);
    executorService.execute(this::repaint);
    executorService.execute(this::reDraw);
  }

  /**
   * v1 readFrame cost 8-30 ms,
   * v2 readFrame cost 8-15 ms
   */
  private void readFrame() {
    while (!Thread.currentThread().isInterrupted()) {
      try {
        TimeUnit.MILLISECONDS.sleep(frameInterval);
        if (screen != null && imageInput != null) {
          BufferedImage read = imageInput.read();
          if (read != null) {
            this.frameManager.setFrame(read);
          }
        }
      } catch (InterruptedException ignored) {
      } catch (Exception e) {
        log.info("readFrame error", e);
      }
    }
  }

  private void repaint() {
    int last = DEFAULT_IMAGE_INPUT.read().hashCode();
    while (!Thread.currentThread().isInterrupted()) {
      try {
        TimeUnit.MILLISECONDS.sleep(renderInterval);
        if (screen != null && imageInput != null) {
          CachedFrame cachedFrame = frameManager.get();
          if (cachedFrame == null) {
            continue;
          }
          int next = cachedFrame.hashCode();
          if (last != next) {
            repaint(cachedFrame.getImage());
            last = next;
          }
        }
      } catch (InterruptedException ignored) {
      } catch (Exception e) {
        log.info("repaint error", e);
      }
    }
  }

  private void reDraw() {
    BufferedImage transparentCanvas = createTransparentCanvas();
    int hash = 0;
    while (!Thread.currentThread().isInterrupted()) {
      try {
        TimeUnit.MILLISECONDS.sleep(renderInterval);
        if (screen != null && !frozenScreen.get() && !drawableHashMap.isEmpty()) {
          int next = 0;
          for (Iterator<Entry<String, Drawable>> iterator = drawableHashMap.entrySet().iterator();
              iterator.hasNext(); ) {
            Entry<String, Drawable> entry = iterator.next();
            Drawable drawable = entry.getValue();
            if (drawable.isExpired()) {
              iterator.remove();
            } else {
              next ^= drawable.hashCode();
            }
          }
          if (next != hash) {
            transparentCanvas = createTransparentCanvas();
            Graphics graphics = transparentCanvas.getGraphics();
            drawableHashMap.values().forEach(drawable -> drawable.draw(graphics));
            canvas.accept(transparentCanvas);
          }
          hash = next;
        }
      } catch (InterruptedException ignored) {
      } catch (Exception e) {
        log.info("reDraw error", e);
      }
    }
  }

  /**
   * v1 resize cost 95 ms
   * v2 resize cost 6 ms, the fastest so far,
   */
  @Subscribe
  public void repaint(BufferedImage frame) {
    if (screen != null && !frozenScreen.get()) {
      BufferedImage resize = ImageResizeUtil.resizeV2(frame, guiConfig.getWidth(), guiConfig.getHeight());
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
      if (drawEvent.getDrawable() != null) {
        drawableHashMap.put(drawEvent.getKey(), drawEvent.getDrawable());
      } else {
        drawableHashMap.remove(drawEvent.getKey());
      }
    }
  }


  public void setImageInput(CameraImageInput imageInput) {
    close();
    this.imageInput = imageInput;
  }

  public void close() {
    if (imageInput != null) {
      imageInput.close();
      imageInput = null;
    }
  }

}
