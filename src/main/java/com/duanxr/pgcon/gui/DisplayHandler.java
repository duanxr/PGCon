package com.duanxr.pgcon.gui;

import com.duanxr.pgcon.config.GuiConfig;
import com.duanxr.pgcon.config.InputConfig;
import com.duanxr.pgcon.event.DrawEvent;
import com.duanxr.pgcon.event.FrameEvent;
import com.duanxr.pgcon.gui.draw.Drawable;
import com.duanxr.pgcon.input.impl.CameraImageInput;
import com.duanxr.pgcon.input.impl.StaticImageInput;
import com.google.common.base.Strings;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
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
public class DisplayHandler {

  private static final StaticImageInput DEFAULT_IMAGE_INPUT = new StaticImageInput(
      "/img/no_input.bmp");
  private static final FrameEvent DEFAULT_FRAME = new FrameEvent(DEFAULT_IMAGE_INPUT.read(), 0L);

  private final Map<String, Drawable> drawableHashMap;

  private final ExecutorService executorService;

  private final AtomicBoolean frozen;

  private final DisplayScreen displayScreen;

  private final GuiConfig guiConfig;

  @Getter
  private CameraImageInput imageInput;

  @Autowired
  public DisplayHandler(DisplayScreen displayScreen,
      InputConfig inputConfig, GuiConfig guiConfig,
      EventBus eventBus, ExecutorService executorService) {
    this.guiConfig = guiConfig;
    this.executorService = executorService;
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
    executorService.execute(() -> {
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

  static BufferedImage deepCopy(BufferedImage bi) {
    ColorModel cm = bi.getColorModel();
    boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
    WritableRaster raster = bi.copyData(null);
    return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
  }

  //todo 透明叠加画布
  @Subscribe
  public void repaint(FrameEvent event) {
    executorService.execute(() -> {
      if (event.getFrame() != null) {
        BufferedImage frame = event.getFrame();
        BufferedImage draw = draw(deepCopy(frame));
        BufferedImage resize = resize(draw, guiConfig.getWidth(),
            guiConfig.getHeight());
        displayScreen.repaint(resize,frame);
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
