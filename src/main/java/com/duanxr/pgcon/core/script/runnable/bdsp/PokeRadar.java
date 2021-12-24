package com.duanxr.pgcon.core.script.runnable.bdsp;

import com.duanxr.pgcon.core.script.BaseScript;
import com.duanxr.pgcon.core.script.ScriptLoader;
import com.duanxr.pgcon.event.DrawEvent;
import com.duanxr.pgcon.event.PGEventBus;
import com.duanxr.pgcon.gui.draw.Line;
import java.awt.Color;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2021/12/9
 */
@Component
public class PokeRadar extends BaseScript {

  private final static AtomicInteger Y_AXIS_BASE = new AtomicInteger(36);
  private final static AtomicInteger Y_AXIS_INTERVAL = new AtomicInteger(82);
  private final static AtomicInteger Y_AXIS_SLOPE = new AtomicInteger(42);
  private final static AtomicInteger Y_AXIS_OFFSET = new AtomicInteger(20);

  private final static AtomicInteger X_AXIS_BASE = new AtomicInteger(45);
  private final static AtomicInteger X_AXIS_INTERVAL = new AtomicInteger(103);
  private final static AtomicInteger X_AXIS_SLOPE = new AtomicInteger(60);

  JPanel sliderPanel = new JPanel();

  private final PGEventBus pgEventBus;

  @Override
  protected void execute() throws Exception {
    int weight = 1920;
    int halfWeight = weight / 2;
    int height = 1080;
    //pokemon.gridAnalyze(this);
    int yb = Y_AXIS_BASE.get();
    int yi = Y_AXIS_INTERVAL.get();
    int ys = Y_AXIS_SLOPE.get();
    int yo = Y_AXIS_OFFSET.get() - 20;
    int i = 0;
    for (int x = halfWeight + yb; x < weight * 2; x += yi) {
      int ye = (int) (x + (x + yb - halfWeight) * (ys / 100D));
      int x1 = x + yo;
      int x2 = ye + yo;
      pgEventBus.post(new DrawEvent(String.valueOf(i++)
          , new Line(x1, 0, x2, height, Color.RED, 100)));
      int x20 = weight - x + yo;
      int x21 = weight - ye + yo;
      pgEventBus.post(new DrawEvent(String.valueOf(i++)
          , new Line(x20, 0, x21, height, Color.RED, 100)));
    }
    int xb = X_AXIS_BASE.get();
    int xi = X_AXIS_INTERVAL.get();
    int xs = X_AXIS_SLOPE.get();
    for (int y = height; y > 0; y -= xi) {
      double y1 = ((height - y * 1D) / height) * (xs);
      if (y1 > xi) {
        break;
      }
      y += y1;
      pgEventBus.post(new DrawEvent(String.valueOf(i++)
          , new Line(0, y-xb, weight, y-xb, Color.RED, 1000)));
    }
    Thread.sleep(30);
  }

  @Override
  public String name() {
    return "Pokemon Radar";
  }

  private JSlider genSlider(AtomicInteger val, int min, int max, int major, int minor) {
    JSlider slider = new JSlider(min, max, val.get());
    slider.setMajorTickSpacing(major);
    slider.setMinorTickSpacing(minor);
    slider.setPaintTicks(false);
    slider.setPaintLabels(false);
    slider.addChangeListener(e -> {
      JSlider source = (JSlider) e.getSource();
      val.set(source.getValue());
    });
    return slider;
  }

  @Autowired
  public PokeRadar(ScriptLoader scriptLoader,
      PGEventBus pgEventBus) {
    super(scriptLoader);
    this.pgEventBus = pgEventBus;
    sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.PAGE_AXIS));
    sliderPanel.add(new JLabel("垂直对称偏移: "));
    sliderPanel.add(genSlider(Y_AXIS_BASE, 20, 60, 5, 1));
    sliderPanel.add(new JLabel("垂直绝对偏移: "));
    sliderPanel.add(genSlider(Y_AXIS_OFFSET, 0, 40, 5, 1));
    sliderPanel.add(new JLabel("垂直矩阵间隔: "));
    sliderPanel.add(genSlider(Y_AXIS_INTERVAL, 60, 100, 5, 1));
    sliderPanel.add(new JLabel("垂直矩阵斜率: "));
    sliderPanel.add(genSlider(Y_AXIS_SLOPE, 20, 100, 5, 1));

    sliderPanel.add(new JLabel("水平绝对偏移: "));
    sliderPanel.add(genSlider(X_AXIS_BASE, 0, 60, 5, 1));
    sliderPanel.add(new JLabel("水平矩阵间隔: "));
    sliderPanel.add(genSlider(X_AXIS_INTERVAL, 95, 125, 5, 1));
    sliderPanel.add(new JLabel("水平矩阵斜率: "));
    sliderPanel.add(genSlider(X_AXIS_SLOPE, 50, 70, 5, 1));
  }

  @Override
  public JPanel label() {
    return sliderPanel;
  }
}
