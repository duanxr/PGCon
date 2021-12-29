/*
package com.duanxr.pgcon.core.util;

import boofcv.abst.feature.detect.interest.ConfigPointDetector;
import boofcv.abst.feature.detect.interest.PointDetectorTypes;
import boofcv.abst.tracker.PointTrack;
import boofcv.abst.tracker.PointTrackerKltPyramid;
import boofcv.alg.filter.derivative.GImageDerivativeOps;
import boofcv.alg.tracker.klt.ConfigPKlt;
import boofcv.factory.tracker.FactoryPointTracker;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayF32;
import boofcv.struct.pyramid.ConfigDiscreteLevels;
import com.duanxr.pgcon.core.fitting.PolyTrendLine;
import com.duanxr.pgcon.core.fitting.PolyTrendLine3D;
import com.duanxr.pgcon.core.script.RunnableScript;
import com.duanxr.pgcon.event.DrawEvent;
import com.duanxr.pgcon.event.FrameEvent;
import com.duanxr.pgcon.gui.draw.Circle;
import com.duanxr.pgcon.gui.draw.Line;
import com.duanxr.pgcon.output.action.ButtonAction;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

*/
/**
 * @author 段然 2021/12/7
 *//*

@Component
public class Pokemon {

  private static final Executor EXECUTOR = Executors.newFixedThreadPool(60);

  private static final int GRID_ANALYZE_TEMPLATE_RADIUS = 3;
  private static final int GRID_ANALYZE_MAX_FEATURES = 180;
  private static final int GRID_ANALYZE_NUM_LEVELS = 4;
  private static final int GRID_ANALYZE_THRESHOLD = 1;
  private static final int GRID_ANALYZE_RADIUS = 6;

  private static final int GRID_BASE_Y_LINE = 570;
  private static final int GRID_BASE_X_LINE = 1025;
  private static final double GRID_FILTER_RANGE_THRESHOLD = 0.1D;
  private static final double GRID_FILTER_MOVING_THRESHOLD = 0.15D;
  private static final double GRID_FILTER_X_THRESHOLD = 10;

  private static final int POLY_TREND_LINE_DEGREE = 1;

  private final EventBus eventBus;

  public Pokemon(@Autowired EventBus eventBus)
      throws InterruptedException {
    this.eventBus = eventBus;
  }


  @SneakyThrows
  public void gridAnalyze(RunnableScript script) {
    initiateCharacter(script);
    PointTrackerKltPyramid<GrayF32, GrayF32> tracker = initiateTracker();
    List<Integer> gridY = drawGridY(script, tracker);
    drawGridX(script, tracker, gridY);
    Thread.sleep(150000);
  }

  private PointTrackerKltPyramid<GrayF32, GrayF32> initiateTracker() {
    Class<GrayF32> imageType = GrayF32.class;
    Class<GrayF32> derivType = GImageDerivativeOps.getDerivativeType(imageType);
    ConfigPKlt configKlt = new ConfigPKlt();
    configKlt.templateRadius = GRID_ANALYZE_RADIUS;
    configKlt.pyramidLevels = ConfigDiscreteLevels.levels(GRID_ANALYZE_NUM_LEVELS);
    ConfigPointDetector configDetector = new ConfigPointDetector();
    configDetector.type = PointDetectorTypes.SHI_TOMASI;
    configDetector.general.maxFeatures = GRID_ANALYZE_MAX_FEATURES;
    configDetector.general.radius = GRID_ANALYZE_RADIUS;
    configDetector.general.threshold = GRID_ANALYZE_THRESHOLD;
    return FactoryPointTracker.klt(configKlt, configDetector, imageType, derivType);
  }

  @SneakyThrows
  private void initiateCharacter(RunnableScript script) {
    script.controller.press(ButtonAction.D_LEFT);
    Thread.sleep(600);
    script.controller.press(ButtonAction.D_RIGHT);
    script.controller.press(ButtonAction.D_RIGHT);
    Thread.sleep(600);
    script.controller.press(ButtonAction.D_TOP);
    script.controller.press(ButtonAction.D_TOP);
    Thread.sleep(600);
    script.controller.press(ButtonAction.D_BOTTOM);
    Thread.sleep(600);
  }

  private void drawGridX(RunnableScript script, PointTrackerKltPyramid<GrayF32, GrayF32> tracker,
      List<Integer> gridY) {
    List<int[]> list = gridAnalyzeMove(script, tracker, ButtonAction.D_LEFT);
    list = filterX(list);
    drawLineHorizon(script, list, gridY);
  }

  @SneakyThrows
  private List<Integer> drawGridY(RunnableScript script,
      PointTrackerKltPyramid<GrayF32, GrayF32> tracker) {
    List<int[]> list = gridAnalyzeMove(script, tracker, ButtonAction.D_BOTTOM);
    list = filterY(list);
    List<Integer> lines = new ArrayList<>();
    lines.add(GRID_BASE_Y_LINE);
    lines.add(1920);
    lines.addAll(drawLineUp(script, list));
    lines.addAll(drawLineDown(script, list));
    lines.sort(Comparator.naturalOrder());
    script.controller.press(ButtonAction.D_TOP);
    script.controller.press(ButtonAction.D_TOP);
    Thread.sleep(600);
    return lines;
  }


  private void drawLineHorizon(RunnableScript script, List<int[]> list,
      List<Integer> gridY) {
    list.sort(Comparator.comparingInt(a -> a[1]));
    Map<Integer, List<int[]>> map = new HashMap<>();
    Map<Integer, Integer> map2 = new HashMap<>();
    int index = 0;
    for (int i = 1; i < gridY.size(); i++) {
      int l = gridY.get(i - 1);
      int r = gridY.get(i);
      map2.putIfAbsent(l, r);
      while (index < list.size() && list.get(index)[1] < l) {
        map.computeIfAbsent(l, ArrayList::new).add(list.get(index));
        index++;
      }
    }
    drawLineXM(script, list, GRID_BASE_Y_LINE, map2.get(GRID_BASE_Y_LINE));
  }

  private List<Integer> drawLineXM(RunnableScript script, List<int[]> list, int l,
      int r) {
    PolyTrendLine3D polyTrendLine = new PolyTrendLine3D(2);
    list.sort(Comparator.comparingInt(o -> o[0]));
    double[][] x = new double[list.size()][2];
    double[] y = new double[list.size()];
    for (int i = 0; i < list.size(); i++) {
      int[] ints = list.get(i);
      System.out.println(Arrays.toString(ints));
      x[i][0] = ints[0];
      x[i][1] = ints[1];
      y[i] = ints[2];
    }
    polyTrendLine.setValues(y, x);
    List<Integer> lines = new ArrayList<>();
    int n = GRID_BASE_X_LINE;
    while (n < 1920) {
      eventBus.post(new DrawEvent("gridAnalyzeLineX" + n + "WithY" + l,
          new Line(n, l, n, r, Color.BLUE)));
      double predict = polyTrendLine.predict(n, (l + r) / 2D);
      n = (int) predict;
      lines.add(n);
    }
    return lines;
  }

  private List<Integer> drawLineDown(RunnableScript script, List<int[]> list) {
    PolyTrendLine polyTrendLine = new PolyTrendLine(POLY_TREND_LINE_DEGREE);
    list.sort(Comparator.comparingInt(o -> o[3]));
    double[] x = new double[list.size()];
    double[] y = new double[list.size()];
    for (int i = 0; i < list.size(); i++) {
      int[] ints = list.get(i);
      x[i] = ints[3];
      y[i] = ints[1];
    }
    polyTrendLine.setValues(y, x);
    List<Integer> lines = new ArrayList<>();
    int n = GRID_BASE_Y_LINE;
    while (n < 1080) {
      eventBus.post(new DrawEvent("gridAnalyzeLineY" + n, new Line(0, n, 1920, n, Color.BLUE)));
      double predict = polyTrendLine.predict(n);
      n = (int) predict;
      lines.add(n);
    }
    return lines;
  }

  private List<Integer> drawLineUp(RunnableScript script, List<int[]> list) {
    PolyTrendLine polyTrendLine = new PolyTrendLine(POLY_TREND_LINE_DEGREE);
    list.sort(Comparator.comparingInt(o -> o[1]));
    double[] x = new double[list.size()];
    double[] y = new double[list.size()];
    for (int i = 0; i < list.size(); i++) {
      int[] ints = list.get(i);
      x[i] = ints[1];
      y[i] = ints[3];
    }
    polyTrendLine.setValues(y, x);
    List<Integer> lines = new ArrayList<>();
    int n = GRID_BASE_Y_LINE;
    while (n > 0) {
      eventBus.post(new DrawEvent("gridAnalyzeLineY" + n, new Line(0, n, 1920, n, Color.BLUE)));
      double predict = polyTrendLine.predict(n);
      n = (int) predict;
      lines.add(n);
    }
    return lines;

  }

  private List<int[]> filterX(List<int[]> list) {
    List<int[]> fun = new ArrayList<>();
    for (int[] ints : list) {
      boolean b1 = !(ints[0] > 790 && ints[0] < 1147 && ints[1] > 312 && ints[1] < 690);
      boolean b2 = ints[0] > GRID_FILTER_RANGE_THRESHOLD * 1920
          && ints[0] < (1 - GRID_FILTER_RANGE_THRESHOLD) * 1920
          && ints[1] > GRID_FILTER_RANGE_THRESHOLD * 1080
          && ints[1] < (1 - GRID_FILTER_RANGE_THRESHOLD) * 1080;
      boolean b3 = ints[0] > (1920 / 2);
      eventBus.post(new DrawEvent(
          String.valueOf(Arrays.hashCode(ints)),
          new Circle(ints[2], ints[3], 9,
              new Color(b1 ? 0 : 255, b2 ? 0 : 255, 255, 90), 33000)));
      if (b1 && b2) {
        ints[1] = (ints[3] + ints[1]) / 2;
        //if (b3) {
        fun.add(ints);
       */
/* } else {
          if (ints[2] < (1920 / 2)) {
            int tmp = ints[2];
            ints[2] = 1920-ints[0];
            ints[0] = 1920-tmp;
            fun.add(ints);
          }
        }*//*

      }
    }
    return fun;
  }

  private List<int[]> filterY(List<int[]> list) {
    int count = 0;
    for (int[] ints : list) {
      count += ints[3] - ints[1];
    }
    count /= list.size();
    count *= GRID_FILTER_MOVING_THRESHOLD;
    List<int[]> fun = new ArrayList<>();
    for (int[] ints : list) {
      //boolean b1 = ints[3] - ints[1] < count;
      boolean b1 = !(ints[0] > 678 && ints[0] < 1147 && ints[1] > 349 && ints[1] < 804);
      boolean b2 = ints[0] > GRID_FILTER_RANGE_THRESHOLD * 1920
          && ints[0] < (1 - GRID_FILTER_RANGE_THRESHOLD) * 1920
          && ints[1] > GRID_FILTER_RANGE_THRESHOLD * 1080
          && ints[1] < (1 - GRID_FILTER_RANGE_THRESHOLD) * 1080;
      eventBus.post(new DrawEvent(
          String.valueOf(Arrays.hashCode(ints)),
          new Circle(ints[2], ints[3], 9,
              new Color(b1 ? 0 : 255, b2 ? 0 : 255, 255, 90), 3000)));
      if (b1 && b2) {
        fun.add(ints);
      }
    }
    return fun;
  }

  public static class GridAnalyzer {

    private final Map<Long, int[]> map = new HashMap<>(GRID_ANALYZE_MAX_FEATURES);
    private final PointTrackerKltPyramid<GrayF32, GrayF32> tracker;
    private final EventBus eventBus;
    private final List<FrameEvent> list = new ArrayList<>(64);

    private GrayF32 frame = null;

    public GridAnalyzer(PointTrackerKltPyramid<GrayF32, GrayF32> tracker,
        EventBus eventBus) {
      this.tracker = tracker;
      this.eventBus = eventBus;
    }

    @Subscribe
    public void handle(FrameEvent frameEvent) {
      list.add(frameEvent);
    }

    private void calculate() {
      tracker.reset();
      list.sort(Comparator.comparingLong(FrameEvent::getTimestamp));
      for (FrameEvent event : list) {
        frame = ConvertBufferedImage.convertFrom(event.getFrame(), frame);
        tracker.process(frame);
        if (map.isEmpty()) {
          tracker.spawnTracks();
        }
        for (PointTrack track : tracker.getActiveTracks(null)) {
          int red = (int) (2.5 * (track.featureId % 100));
          int green = (int) ((255.0 / 150.0) * (track.featureId % 150));
          int blue = (int) (track.featureId % 255);
          eventBus.post(new DrawEvent(String.valueOf(track.featureId),
              new Circle((int) track.pixel.x, (int) track.pixel.y, 9,
                  new Color(red, green, blue, 180), 10)));
          map.compute(track.featureId, (k, v) -> {
            if (v == null) {
              v = new int[]{(int) track.pixel.x, (int) track.pixel.y, 0, 0};
            }
            v[2] = (int) track.pixel.x;
            v[3] = (int) track.pixel.y;
            return v;
          });
        }
      }
    }


    public List<int[]> getResult() {
      calculate();
      List<PointTrack> activeTracks = tracker.getActiveTracks(null);
      List<int[]> list = new ArrayList<>(activeTracks.size());
      for (PointTrack track : activeTracks) {
        list.add(map.get(track.featureId));
      }
      return list;
    }


  }

  @SneakyThrows
  private List<int[]> gridAnalyzeMove(RunnableScript script,
      PointTrackerKltPyramid<GrayF32, GrayF32> tracker,
      ButtonAction action) {
    script.controller.press(action);
    Thread.sleep(1200);
    GridAnalyzer gridAnalyzer = new GridAnalyzer(tracker, eventBus);
    eventBus.register(gridAnalyzer);
    Thread.sleep(1000);
    script.controller.press(action);
    Thread.sleep(1200);
    eventBus.unregister(gridAnalyzer);
    return gridAnalyzer.getResult();
  }


}
*/
