package com.duanxr.pgcon.core.script.runnable.bdsp;

import com.duanxr.pgcon.core.PGCon;
import com.duanxr.pgcon.core.detect.Area;
import com.duanxr.pgcon.core.detect.image.compare.ImageCompare.Method;
import com.duanxr.pgcon.core.detect.image.compare.ImageCompare.Param;
import com.duanxr.pgcon.core.detect.image.compare.ImageCompare.Param.Period;
import com.duanxr.pgcon.core.detect.image.compare.ImageCompare.Result;
import com.duanxr.pgcon.core.script.RunnableScript;
import com.duanxr.pgcon.core.script.ScriptLoader;
import com.duanxr.pgcon.event.DrawEvent;
import com.duanxr.pgcon.gui.draw.Circle;
import com.duanxr.pgcon.gui.draw.Line;
import com.duanxr.pgcon.gui.draw.Rectangle;
import com.duanxr.pgcon.gui.draw.Text;
import com.duanxr.pgcon.util.MatUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.util.Pair;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import lombok.SneakyThrows;
import org.opencv.core.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2021/12/9
 */
@Component
public class PokeRadar extends RunnableScript {

  private final static AtomicInteger VERTICAL_AXIS_BASE = new AtomicInteger(36);
  private final static AtomicInteger VERTICAL_AXIS_INTERVAL = new AtomicInteger(82);
  private final static AtomicInteger VERTICAL_AXIS_SLOPE = new AtomicInteger(42);
  private final static AtomicInteger VERTICAL_AXIS_OFFSET = new AtomicInteger(20);

  private final static AtomicInteger HORIZON_AXIS_BASE = new AtomicInteger(45);
  private final static AtomicInteger HORIZON_AXIS_INTERVAL = new AtomicInteger(103);
  private final static AtomicInteger HORIZON_AXIS_SLOPE = new AtomicInteger(60);

  private final static AtomicBoolean RUN = new AtomicBoolean(false);

  private final JPanel extraPanel;

  private final int weight = pg.getInputConfig().getWidth();
  private final int height = pg.getInputConfig().getHeight();

  private final int halfWeight = weight / 2;

  private int xOffset = 0;
  private int yOffset = 0;
  private Point[][] grid = null;


  @Autowired
  public PokeRadar(@Autowired PGCon pg) {
    super(pg);
    this.extraPanel = new JPanel();
    extraPanel.setLayout(new BoxLayout(extraPanel, BoxLayout.PAGE_AXIS));
    extraPanel.add(new JLabel("垂直对称偏移: "));
    extraPanel.add(genSlider(VERTICAL_AXIS_BASE, 20, 60));
    extraPanel.add(new JLabel("垂直绝对偏移: "));
    extraPanel.add(genSlider(VERTICAL_AXIS_OFFSET, 0, 40));
    extraPanel.add(new JLabel("垂直矩阵间隔: "));
    extraPanel.add(genSlider(VERTICAL_AXIS_INTERVAL, 60, 100));
    extraPanel.add(new JLabel("垂直矩阵斜率: "));
    extraPanel.add(genSlider(VERTICAL_AXIS_SLOPE, 20, 100));
    extraPanel.add(new JLabel("水平绝对偏移: "));
    extraPanel.add(genSlider(HORIZON_AXIS_BASE, 0, 60));
    extraPanel.add(new JLabel("水平矩阵间隔: "));
    extraPanel.add(genSlider(HORIZON_AXIS_INTERVAL, 95, 125));
    extraPanel.add(new JLabel("水平矩阵斜率: "));
    extraPanel.add(genSlider(HORIZON_AXIS_SLOPE, 50, 70));
    JButton button = new JButton("Run!");
    button.addActionListener(e -> {
      RUN.set(false);
      button.setText("Running...");
    });
    extraPanel.add(button);
  }

  private JSlider genSlider(AtomicInteger val, int min, int max) {
    JSlider slider = new JSlider(min, max, val.get());
    slider.setMajorTickSpacing(5);
    slider.setMinorTickSpacing(1);
    slider.setPaintTicks(false);
    slider.setPaintLabels(false);
    slider.addChangeListener(e -> {
      JSlider source = (JSlider) e.getSource();
      val.set(source.getValue());
    });
    return slider;
  }

  @Override
  public String getName() {
    return "Pokemon Radar";
  }


  @Override
  public void init() {
    extraPanel.setEnabled(true);
    addExtraPanel(extraPanel);
    while (!RUN.get() || grid == null) {
      int yb = VERTICAL_AXIS_BASE.get();
      int yi = VERTICAL_AXIS_INTERVAL.get();
      int ys = VERTICAL_AXIS_SLOPE.get();
      int yo = VERTICAL_AXIS_OFFSET.get() - 20;
      int i = 0;
      LinkedList<Pair<Point, Point>> ylist = new LinkedList<>();
      for (int x = halfWeight + yb; x < weight; x += yi) {
        int ye = (int) (x + (x + yb - halfWeight) * (ys / 100D));
        int x1 = x + yo;
        int x2 = ye + yo;
        eventBus.post(new DrawEvent(String.valueOf(i++)
            , new Line(x1, 0, x2, height, Color.RED, 100)));
        ylist.addLast(new Pair<>(new Point(x1, 0), new Point(x2, height)));
        int x20 = weight - x + yo;
        int x21 = weight - ye + yo;
        eventBus.post(new DrawEvent(String.valueOf(i++)
            , new Line(x20, 0, x21, height, Color.RED, 100)));
        ylist.addFirst(new Pair<>(new Point(x20, 0), new Point(x21, height)));
      }
      List<Pair<Point, Point>> xlist = new ArrayList<>();
      xlist.add(new Pair<>(new Point(0, height), new Point(weight, height)));
      int xb = HORIZON_AXIS_BASE.get();
      int xi = HORIZON_AXIS_INTERVAL.get();
      int xs = HORIZON_AXIS_SLOPE.get();
      for (int y = height; y > 0; y -= xi) {
        double y1 = ((height - y * 1D) / height) * (xs);
        if (y1 > xi) {
          break;
        }
        y += y1;
        eventBus.post(new DrawEvent(String.valueOf(i++)
            , new Line(0, y - xb, weight, y - xb, Color.RED, 1000)));
        xlist.add(new Pair<>(new Point(0, y - xb), new Point(weight, y - xb)));
      }
      xlist.add(new Pair<>(new Point(0, 0), new Point(weight, 0)));
      grid = new Point[xlist.size()][ylist.size()];
      for (int ix = xlist.size() - 1; ix >= 0; ix--) {
        Pair<Point, Point> x = xlist.get(ix);
        for (int iy = 0, ylistSize = ylist.size(); iy < ylistSize; iy++) {
          Pair<Point, Point> y = ylist.get(iy);
          Point p = MatUtil.calculateInterceptionPoint(x.getKey(), x.getValue(), y.getKey(),
              y.getValue());
          grid[ix][iy] = p;
        }
      }
      OUT:
      for (int ix = 0, gridLength = grid.length; ix < gridLength; ix++) {
        Point[] points = grid[ix];
        for (int iy = 0, pointsLength = points.length; iy < pointsLength; iy++) {
          Point point = points[iy];
          if (point.y > height / 2) {
            continue OUT;
          }
          if (point.x > weight / 2) {
            yOffset = iy - 1;
            xOffset = ix - 1;
            break OUT;
          }
        }
      }

      sleep(30);
    }
    extraPanel.setEnabled(false);
  }

  @Override
  public void clear() {
  }

  @Override
  protected void execute() {
    detectShakingGrass();
  }

  @SneakyThrows
  private void detectShakingGrass() {
    long s =  System.currentTimeMillis();
    List<Pair<Pair<Integer, Integer>, Future<Result>>> list = new ArrayList<>();
    for (int x = 0, gridLength = grid.length; x < gridLength - 1; x++) {
      for (int y = 0, pointsLength = grid[0].length; y < pointsLength - 1; y++) {
        Point point = grid[x][y];
        Point point2 = grid[x + 1][y + 1];
        if (point.x > 0 && point.x < weight && point.y > 0 && point.y < height &&
            point2.x > 0 && point2.x < weight && point2.y > 0 && point2.y < height) {
          int offX = x + xOffset;
          int offY = y + yOffset;
          if ((offX != 0 || offY != 0) && (offX != 0 || offY != 1)) {
            Future<Result> detect = imageCompare.asyncDetect(Param.builder()
                .image("").method(Method.ORB).period(Period.builder().frames(10).build())
                .area(Area.ofPoints((int) point.x, (int) point.y, (int) point2.x, (int) point2.y))
                .build());
            list.add(new Pair<>(new Pair<>(x, y), detect));
          }
        }
      }
    }
    //get =>
    list.sort(new Comparator<>() {
      @Override
      @SneakyThrows
      public int compare(Pair<Pair<Integer, Integer>, Future<Result>> o1,
          Pair<Pair<Integer, Integer>, Future<Result>> o2) {
        return Float.compare(o1.getValue().get().getMax().getPoint(),
            o2.getValue().get().getMax().getPoint());
      }
    });
    for (int i = 0, listSize = list.size(); i < listSize; i++) {
      Pair<Pair<Integer, Integer>, Future<Result>> pair = list.get(i);
      int x = pair.getKey().getKey();
      int y = pair.getKey().getValue();
      Point point = grid[x][y];
      Point point2 = grid[x + 1][y + 1];
      if(i<4) {
        eventBus.post(new DrawEvent("pkr-sg-" + x + "-" + y,
            new Rectangle(Area.ofPoints(point.x,point.y,point2.x,point2.y), Color.ORANGE, 100)));
      }
      eventBus.post(new DrawEvent("pkr-pt-" + x + "-" + y,
          new Text(Area.ofRect(x, y - 30, 40, 40),
              String.valueOf(pair.getValue().get().getMax().getPoint()),
              Color.GREEN, 24, 100)));
    }
    System.out.println("detectShakingGrass: " + (System.currentTimeMillis() - s));
  }
}
