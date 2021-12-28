package com.duanxr.pgcon.core.detect.image.compare;

import com.duanxr.pgcon.core.ComponentManager;
import com.duanxr.pgcon.core.detect.FrameCache;
import com.duanxr.pgcon.core.detect.FrameReceiver;
import com.duanxr.pgcon.core.detect.image.compare.ImageCompare.Result.Similarity;
import com.duanxr.pgcon.event.FrameEvent;
import com.duanxr.pgcon.util.MatUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.NonNull;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.ORB;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2021/12/6
 */
@Component
public class OpenCvImageCompare implements ImageCompare {

  private final Map<Method, BiFunction<Mat, Mat, Float>> methods;
  private final ComponentManager componentManager;
  private final FrameCache frameCache;

  @Autowired
  public OpenCvImageCompare(ComponentManager componentManager,
      FrameCache frameCache) {
    this.componentManager = componentManager;
    this.frameCache = frameCache;
    this.methods = new HashMap<>();
    methods.put(Method.ORB, this::orb);
  }

  @Override
  public Future<Result> asyncDetect(Param param) {
    return componentManager.getExecutors().submit(() -> detect(param));
  }


  @Override
  public Result detect(Param param) {
    Mat temple = Imgcodecs.imread(param.getImage(), Imgcodecs.IMREAD_COLOR);
    return param.getPeriod() == null || param.getPeriod().getFrames() == 0 ?
        detectNow(temple, param) : detectPeriod(temple, param);
  }

  private Result detectPeriod(Mat temple, Param param) {
    List<Similarity> list = new ArrayList<>(param.getPeriod().getFrames());
    Function<Similarity, Boolean> checker = param.getPeriod().getChecker();
    if (param.getPeriod().getFrames() < 0) {
      List<FrameEvent> eventList = frameCache.get(-param.getPeriod().getFrames());
      for (FrameEvent frame : eventList) {
        Mat originMat = MatUtil.toMat(frame.getFrame());
        Mat targetMat = MatUtil.split(originMat, param.getArea());
        Float score = doDetect(temple, targetMat,param.getMethod());
        Similarity similarity = Similarity.builder().point( score)
            .timestamp(frame.getTimestamp())
            .build();
        list.add(similarity);
        if(checker!=null && checker.apply(similarity)){
          break;
        }
      }
    } else {
      FrameReceiver receiver = new FrameReceiver(componentManager, param.getPeriod().getFrames()) {
        @Override
        public void receive(FrameEvent frame) {
          Mat originMat = MatUtil.toMat(frame.getFrame());
          Mat targetMat = MatUtil.split(originMat, param.getArea());
          Float score = doDetect(temple, targetMat,param.getMethod());
          Similarity similarity = Similarity.builder().point( score)
              .timestamp(frame.getTimestamp())
              .build();
          list.add(similarity);
          if(checker!=null && checker.apply(similarity)){
            super.breakReceive();
          }
        }
      };
    }
    Similarity max = list.get(0);
    Similarity min = list.get(0);
    float sum = 0;
    for (Similarity similarity : list) {
      if (similarity.getPoint() > max.getPoint()) {
        max = similarity;
      }
      if (similarity.getPoint() < min.getPoint()) {
        min = similarity;
      }
      sum += similarity.getPoint();
    }
    Similarity avg = Similarity.builder().point(sum / list.size()).timestamp(
            (list.get(0).getTimestamp() + list.get(list.size() - 1).getTimestamp()) / 2)
        .build();
    return Result.builder().avg(avg).max(max).min(min)
        .all(list).build();
  }

  private Result detectNow(Mat temple, @NonNull Param param) {
    FrameEvent frame = frameCache.get();
    Mat originMat = MatUtil.toMat(frame.getFrame());
    Mat targetMat = MatUtil.split(originMat, param.getArea());
    Float score = doDetect(temple, targetMat,param.getMethod());
    Similarity similarity = Similarity.builder().point( score)
        .timestamp(frame.getTimestamp())
        .build();
    return Result.builder().avg(similarity).max(similarity).min(similarity)
        .all(Collections.singletonList(similarity)).build();
  }

  private Float doDetect(Mat temple, Mat target, Method method) {
    return methods.get(method).apply(temple, target);
  }

  private Float orb(Mat temple, Mat target) {
    int retVal = 0;
    MatOfKeyPoint keyPoint1 = new MatOfKeyPoint();
    MatOfKeyPoint keyPoint2 = new MatOfKeyPoint();
    Mat descriptors1 = new Mat();
    Mat descriptors2 = new Mat();
    //TODO 复用?
    ORB detector = ORB.create();
    detector.detect(temple, keyPoint1);
    detector.detect(target, keyPoint2);
    detector.compute(temple, keyPoint1, descriptors1);
    detector.compute(target, keyPoint2, descriptors2);
    DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
    MatOfDMatch matches = new MatOfDMatch();
    if (descriptors2.cols() == descriptors1.cols()) {
      matcher.match(descriptors1, descriptors2, matches);
      DMatch[] match = matches.toArray();
      double maxDist = 0;
      double minDist = 100;
      for (int i = 0; i < descriptors1.rows(); i++) {
        double dist = match[i].distance;
        if (dist < minDist) {
          minDist = dist;
        }
        if (dist > maxDist) {
          maxDist = dist;
        }
      }
      System.out.println("maxDist=" + maxDist + ", minDist=" + minDist);
      for (int i = 0; i < descriptors1.rows(); i++) {
        if (match[i].distance <= 10) {
          retVal++;
        }
      }
      System.out.println("matching count=" + retVal);
    }
    return (float) retVal;
  }


}
