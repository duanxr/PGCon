package com.duanxr.rhm.cache.entity;

import lombok.Data;
import org.opencv.core.Mat;

/**
 * @author Duanran 2019/12/13
 */
@Data
public class CachedImageTemplate {

  private String name;
  private Mat template;
  private Mat mask;

  public boolean hasMask() {
    return mask != null;
  }
}