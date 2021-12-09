package com.duanxr.pgcon.gui;


import static com.duanxr.pgcon.util.ConstantConfig.INPUT_VIDEO_FRAME_INTERVAL;

import com.duanxr.pgcon.gui.draw.Drawable;
import com.duanxr.pgcon.input.CameraImageInput;
import com.duanxr.pgcon.input.StaticImageInput;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.ImageIcon;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

/**
 * @author Duanran 2019/12/21
 */
@Service
public class DisplayHandler {

  private static final StaticImageInput DEFAULT_IMAGE_INPUT = new StaticImageInput(
      "/img/no_input.bmp");

  private final Map<String, Drawable> drawableHashMap = new ConcurrentHashMap<>();

  private final AtomicBoolean frozen = new AtomicBoolean(false);

  @Getter
  private CameraImageInput imageInput;
  private DisplayScreen displayScreen;

  public DisplayHandler() {
    Executors.newSingleThreadExecutor().execute(new Runnable() {
      @Override
      @SneakyThrows
      public void run() {
        while (true) {
          TimeUnit.MILLISECONDS.sleep(INPUT_VIDEO_FRAME_INTERVAL);
          if (displayScreen != null && imageInput != null && !frozen.get()) {
            repaint(getDisplay());
          }
        }
      }
    });
  }

  public BufferedImage getDisplay() {
    return getImageInput().read();
  }

  public void addDrawRule(String key, Drawable drawable) {
    drawableHashMap.put(key, drawable);
  }

  public void removeDrawRule(String key) {
    drawableHashMap.remove(key);
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

  public void setDisplayScreen(DisplayScreen displayScreen) {
    this.displayScreen = displayScreen;
    repaint(DEFAULT_IMAGE_INPUT.read());
  }

  private void repaint(BufferedImage image) {
    ImageIcon icon = new ImageIcon(draw(image));
    displayScreen.setIcon(icon);
    displayScreen.repaint();
  }
}
