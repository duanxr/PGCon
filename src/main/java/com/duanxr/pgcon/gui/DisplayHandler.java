package com.duanxr.pgcon.gui;

import com.duanxr.pgcon.config.GuiConfig;
import com.duanxr.pgcon.config.InputConfig;
import com.duanxr.pgcon.core.PGPool;
import com.duanxr.pgcon.event.DrawEvent;
import com.duanxr.pgcon.event.FrameEvent;
import com.duanxr.pgcon.gui.draw.Drawable;
import com.duanxr.pgcon.input.CameraImageInput;
import com.duanxr.pgcon.input.StaticImageInput;
import com.google.common.base.Strings;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Getter;
import lombok.SneakyThrows;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Duanran 2019/12/21
 */
@Service
public class DisplayHandler {

  private static final StaticImageInput DEFAULT_IMAGE_INPUT = new StaticImageInput(
      "/img/no_input.bmp");
  private static final FrameEvent DEFAULT_FRAME = new FrameEvent(DEFAULT_IMAGE_INPUT.read(), 0L);

  private final Map<String, Drawable> drawableHashMap;

  private final AtomicBoolean frozen;

  private final DisplayScreen displayScreen;

  private final GuiConfig guiConfig;

  private final PGPool pgPool;

  @Getter
  private CameraImageInput imageInput;

  @Autowired
  public DisplayHandler(DisplayScreen displayScreen,
      InputConfig inputConfig, GuiConfig guiConfig, PGPool pgPool,
      EventBus eventBus) {
    this.guiConfig = guiConfig;
    this.pgPool = pgPool;
    this.drawableHashMap = new ConcurrentHashMap<>();
    this.frozen = new AtomicBoolean(false);
    this.displayScreen = displayScreen;
    Executors.newSingleThreadExecutor().execute(() -> {
      while (!Thread.currentThread().isInterrupted()) {
        try {
          TimeUnit.MILLISECONDS.sleep(inputConfig.getFrameInterval());
          if (DisplayHandler.this.displayScreen != null && imageInput != null && !frozen.get()) {
            BufferedImage image = getDisplay();
            FrameEvent imageEvent = new FrameEvent(image, System.currentTimeMillis());
            eventBus.post(imageEvent);
          }
        } catch (InterruptedException ignored) {
        }
      }
    });
    repaint(DEFAULT_FRAME);
    eventBus.register(this);
  }

  public BufferedImage getDisplay() {
    return getImageInput().read();
  }

  @Subscribe
  public void handleDrawEvent(DrawEvent drawEvent) {
    pgPool.getExecutors().execute(() -> {
      if (!Strings.isNullOrEmpty(drawEvent.getKey())) {
        if (drawEvent.getDrawable() == null) {
          drawableHashMap.remove(drawEvent.getKey());
        } else {
          drawableHashMap.put(drawEvent.getKey(), drawEvent.getDrawable());
        }
      }
    });
  }

  private BufferedImage draw(BufferedImage read) {
    Graphics graphics = read.getGraphics();
    for (Iterator<Entry<String, Drawable>> iterator = drawableHashMap.entrySet().iterator();
        iterator.hasNext(); ) {
      Entry<String, Drawable> entry = iterator.next();
      Drawable drawable = entry.getValue();
      drawable.draw(graphics);
      if (drawable.isExpired()) {
        iterator.remove();
      }
    }
    return read;
  }

  private boolean froze() {
    return frozen.getAndSet(true);
  }

  private boolean unfroze() {
    return frozen.getAndSet(false);
  }

  public void close() {
    if (imageInput != null) {
      imageInput.close();
      imageInput = null;
    }
  }

  public void setImageInput(CameraImageInput imageInput) {
    close();
    this.imageInput = imageInput;
  }


  @Subscribe
  public void repaint(FrameEvent event) {
    pgPool.getExecutors().execute(() -> {
      if (event.getFrame() != null) {
        BufferedImage draw = draw(event.getFrame());
        BufferedImage resize = resize(draw, guiConfig.getWidth(),
            guiConfig.getHeight());
        displayScreen.repaint(resize);
      } else {
        repaint(DEFAULT_FRAME);
      }
    });
  }

  @SneakyThrows
  public BufferedImage resize(BufferedImage bufferedImage, int width, int height) {
    return Thumbnails.of(bufferedImage).size(width, height).asBufferedImage();
  }
}
