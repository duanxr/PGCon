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
import com.duanxr.pgcon.core.script.BaseScript;
import com.duanxr.pgcon.gui.DisplayHandler;
import com.duanxr.pgcon.gui.draw.Circle;
import com.duanxr.pgcon.gui.draw.Line;
import com.duanxr.pgcon.input.CameraImageInput;
import com.duanxr.pgcon.output.action.ButtonAction;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2021/12/7
 */
@Component
public class Pokemon {

  private static final int GRID_ANALYZE_TEMPLATE_RADIUS = 3;
  private static final int GRID_ANALYZE_MAX_FEATURES = 150;
  private static final int GRID_ANALYZE_NUM_LEVELS = 4;
  private static final int GRID_ANALYZE_THRESHOLD = 1;
  private static final int GRID_ANALYZE_RADIUS = 6;
  @Autowired
  private DisplayHandler displayHandler;

  @SneakyThrows
  public void gridAnalyze(BaseScript script) {

    Class imageType = GrayF32.class;
    Class derivType = GImageDerivativeOps.getDerivativeType(imageType);
    ConfigPKlt configKlt = new ConfigPKlt();
    configKlt.templateRadius = GRID_ANALYZE_RADIUS;
    configKlt.pyramidLevels = ConfigDiscreteLevels.levels(GRID_ANALYZE_NUM_LEVELS);
    ConfigPointDetector configDetector = new ConfigPointDetector();
    configDetector.type = PointDetectorTypes.SHI_TOMASI;
    configDetector.general.maxFeatures = GRID_ANALYZE_MAX_FEATURES;
    configDetector.general.radius = GRID_ANALYZE_RADIUS;
    configDetector.general.threshold = GRID_ANALYZE_THRESHOLD;
    PointTrackerKltPyramid tracker = FactoryPointTracker.klt(configKlt, configDetector, imageType,
        derivType);
    List<int[]> list = gridAnalyze0move(script, tracker, ButtonAction.D_BOTTOM);

    int count = 0;
    for (int[] ints : list) {
      count += ints[3] - ints[1];
    }
    count /= list.size();
    count *= 0.25F;
    List<int[]> fun = new ArrayList<>();
    for (int[] ints : list) {
      displayHandler.addDrawRule(
          String.valueOf(ints.hashCode()),
          new Circle(ints[2], ints[3], 9,
              new Color(255, ints[3] - ints[1] > count ? 255 : 0, 0, 180), 5000));
      if (ints[3] - ints[1] < count) {
        fun.add(new int[]{ints[1], ints[3] - ints[1]});
      }
    }
    Thread.sleep(5000);
    PolyTrendLine polyTrendLine = new PolyTrendLine(1);
    fun.sort(Comparator.comparingInt(o -> o[0]));
    double[] x = new double[fun.size()];
    double[] y = new double[fun.size()];
    for (int i = 0; i < fun.size(); i++) {
      int[] ints = fun.get(i);
      x[i] = ints[0];
      y[i] = ints[1];
    }
    polyTrendLine.setValues(y,x);
    System.out.println(Arrays.toString(x));
    System.out.println(Arrays.toString(y));
    int n = 400;
    while (n>0) {
      displayHandler.addDrawRule("gridAnalyzeLineY"+n,new Line(0,n,1280,n,Color.BLUE));
      double predict = polyTrendLine.predict(n);
      System.out.println("n:"+n+"  predict:"+predict);
      n+= (int) predict;
    }
    script.controller.press(ButtonAction.D_TOP);
    Thread.sleep(15000);
    //gridAnalyze0move(script, tracker, ButtonAction.D_TOP);
  }

  @SneakyThrows
  private List<int[]> gridAnalyze0move(BaseScript script, PointTrackerKltPyramid tracker,
      ButtonAction action) {
    Map<Long, int[]> map = new HashMap<>(GRID_ANALYZE_MAX_FEATURES);
    boolean isFirst = true;
    CameraImageInput imageInput = displayHandler.getImageInput();
    BufferedImage image = imageInput.read(true);
    GrayF32 frame = null;
    frame = ConvertBufferedImage.convertFrom(image, frame);
    script.executors.execute(() -> {
      try {
        Thread.sleep(800);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      script.controller.press(action);
    });
    long start = System.currentTimeMillis();
    do {
      BufferedImage image0 = imageInput.read(true);
      if (image != image0) {
        image = image0;
        frame = ConvertBufferedImage.convertFrom(image, frame);
        tracker.process(frame);
        if (isFirst) {
          tracker.spawnTracks();
        }
        for (Object track : tracker.getActiveTracks(null)) {
          PointTrack p = (PointTrack) track;
          int red = (int) (2.5 * (p.featureId % 100));
          int green = (int) ((255.0 / 150.0) * (p.featureId % 150));
          int blue = (int) (p.featureId % 255);
          displayHandler.addDrawRule(
              String.valueOf(p.featureId),
              new Circle((int) p.pixel.x, (int) p.pixel.y, 9,
                  new Color(red, green, blue, 180), 10));
          if (isFirst) {
            map.put(p.featureId, new int[]{(int) p.pixel.x, (int) p.pixel.y, 0, 0});
          }
        }
        isFirst = false;
      }
      TimeUnit.MILLISECONDS.sleep(5);
    } while (System.currentTimeMillis() - start < 2000);
    List activeTracks = tracker.getActiveTracks(null);
    List<int[]> list = new ArrayList<>(activeTracks.size());
    for (Object track : activeTracks) {
      PointTrack p = (PointTrack) track;
      int[] ints = map.get(p.featureId);
      if (ints != null) {
        ints[2] = (int) p.pixel.x;
        ints[3] = (int) p.pixel.y;
        list.add(ints);
      }
    }
    return list;
  }
}
