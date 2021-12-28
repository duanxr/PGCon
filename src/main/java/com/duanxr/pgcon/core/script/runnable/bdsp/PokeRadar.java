package com.duanxr.pgcon.core.script.runnable.bdsp;

import boofcv.alg.background.BackgroundModelStationary;
import boofcv.factory.background.ConfigBackgroundBasic;
import boofcv.factory.background.FactoryBackgroundModel;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.ImageBase;
import boofcv.struct.image.ImageType;
import com.duanxr.pgcon.core.detect.base.Area;
import com.duanxr.pgcon.core.script.BaseScript;
import com.duanxr.pgcon.core.script.ScriptLoader;
import com.duanxr.pgcon.event.DrawEvent;
import com.duanxr.pgcon.event.FrameEvent;
import com.duanxr.pgcon.event.EventBus;
import com.duanxr.pgcon.gui.draw.Circle;
import com.duanxr.pgcon.gui.draw.Line;
import com.duanxr.pgcon.gui.draw.Text;
import com.google.common.eventbus.Subscribe;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.util.Pair;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import lombok.SneakyThrows;
import nu.pattern.OpenCV;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
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

  private final static AtomicBoolean RUN = new AtomicBoolean(false);


  JPanel sliderPanel = new JPanel();

  private final EventBus eventBus;

  int zx = 0;
  int zy = 0;
  Point[][] grid = null;

  @Override
  protected void execute() throws Exception {
    OpenCV.loadLocally();
    int weight = 1920;
    int halfWeight = weight / 2;
    int height = 1080;
    if (!RUN.get()) {
      //pokemon.gridAnalyze(this);
      int yb = Y_AXIS_BASE.get();
      int yi = Y_AXIS_INTERVAL.get();
      int ys = Y_AXIS_SLOPE.get();
      int yo = Y_AXIS_OFFSET.get() - 20;
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
      int xb = X_AXIS_BASE.get();
      int xi = X_AXIS_INTERVAL.get();
      int xs = X_AXIS_SLOPE.get();
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
          Point p = calculateInterceptionPoint(x.getKey(), x.getValue(), y.getKey(), y.getValue());
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
            zy = iy - 1;
            zx = ix - 1;
            break OUT;
          }
        }
      }

      for (int ix = 0, gridLength = grid.length; ix < gridLength; ix++) {
        Point[] points = grid[ix];
        for (int iy = 0, pointsLength = points.length; iy < pointsLength; iy++) {
          Point p = points[iy];
          eventBus.post(new DrawEvent("gP-" + ix + "-" + iy,
              new Circle((int) p.x, (int) p.y, 10, Color.ORANGE, 100)));
          eventBus.post(new DrawEvent("gT-" + ix + "-" + iy,
              new Text(new Area((int) p.x, (int) p.y - 30, 40, 40), (ix - zx) + "," + (iy - zy),
                  Color.GREEN, 24, 100)));
        }
      }
      Thread.sleep(30);
    } else if (grid != null) {
      int[][] map = new int[grid.length][grid[0].length];
      ImageType imageType = ImageType.single(GrayF32.class);
      BackgroundModelStationary background =
          FactoryBackgroundModel.stationaryBasic(new ConfigBackgroundBasic(30, 0.01F), imageType);
      GrayU8 segmented = new GrayU8(1920, 1080);
      int[][] re = new int[1920][1080];
      ImageLoader loader = new ImageLoader(eventBus, 20);
      List<BufferedImage> bufferedImages = loader.get();
      for (BufferedImage bufferedImage : bufferedImages) {
        ImageBase image = ConvertBufferedImage.convertFromSingle(bufferedImage, null,
            GrayF32.class);
        background.updateBackground(image, segmented);
        for (int i = 0; i < re.length; i++) {
          for (int j = 0; j < re[i].length; j++) {
            re[i][j] += segmented.get(i, j);
          }
        }
      }

      for (int ix = 0, gridLength = grid.length; ix < gridLength - 1; ix++) {
        for (int iy = 0, pointsLength = grid[0].length; iy < pointsLength - 1; iy++) {
          Point point = grid[ix][iy];
          Point point2 = grid[ix + 1][iy + 1];
          if (point.x > 0 && point.x < weight && point.y > 0 && point.y < height &&
              point2.x > 0 && point2.x < weight && point2.y > 0 && point2.y < height) {
            for (int xx = (int) point.x; xx < point2.x; xx++) {
              for (int yy = (int) point2.y; yy < point.y; yy++) {
                try {
                  MatOfPoint2f matOfPoint2f = new MatOfPoint2f(point, new Point(point.x, point2.y),
                      point2, new Point(point2.x, point.y));
                  double test = Imgproc.pointPolygonTest(matOfPoint2f, new Point(xx, yy), false);
                  if (test > 0) {
                    map[ix][iy] += re[xx][yy];
                  }
                } catch (Throwable e) {
                  e.printStackTrace();
                }

              }
            }

          }
        }
      }

      for (int ix = 0, gridLength = grid.length; ix < gridLength; ix++) {
        Point[] points = grid[ix];
        for (int iy = 0, pointsLength = points.length; iy < pointsLength; iy++) {
          Point p = points[iy];
          eventBus.post(new DrawEvent("gP-" + ix + "-" + iy,
              new Circle((int) p.x, (int) p.y, 10, Color.ORANGE, 5600)));
          eventBus.post(new DrawEvent("gT-" + ix + "-" + iy,
              new Text(new Area((int) p.x, (int) p.y - 30, 40, 40), String.valueOf(map[ix][iy]),
                  Color.GREEN, 24, 5600)));
        }
      }
    }
  }

  public static class ImageLoader {

    List<BufferedImage> list;
    int size;
    EventBus eventBus;
    CountDownLatch countDownLatch;

    public ImageLoader(EventBus eventBus, int size) {
      list = new ArrayList<>(size);
      this.size = size;
      this.countDownLatch = new CountDownLatch(size);
      this.eventBus = eventBus;
      eventBus.register(this);
    }

    @Subscribe
    public void onImage(FrameEvent event) {
      BufferedImage image = event.getFrame();
      if (image != null) {
        list.add(image);
      }
      if (list.size() == size) {
        eventBus.unregister(this);
      }
      countDownLatch.countDown();
    }

    @SneakyThrows
    public List<BufferedImage> get() {
      countDownLatch.await();
      return list;
    }
  }

  public static Point calculateInterceptionPoint(Point s1, Point s2, Point d1, Point d2) {

    double a1 = s2.y - s1.y;
    double b1 = s1.x - s2.x;
    double c1 = a1 * s1.x + b1 * s1.y;

    double a2 = d2.y - d1.y;
    double b2 = d1.x - d2.x;
    double c2 = a2 * d1.x + b2 * d1.y;

    double delta = a1 * b2 - a2 * b1;
    return new Point((float) ((b2 * c1 - b1 * c2) / delta),
        (float) ((a1 * c2 - a2 * c1) / delta));

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
      EventBus eventBus) {
    super(scriptLoader);
    this.eventBus = eventBus;
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

    JButton button = new JButton("运行");
    button.addActionListener(e -> {
      if (RUN.get()) {
        RUN.set(false);
        button.setText("暂停");
      } else {
        RUN.set(true);
        button.setText("运行");
      }
    });
    sliderPanel.add(button);
  }

  @Override
  public JPanel label() {
    return sliderPanel;
  }
}
