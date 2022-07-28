package com.duanxr.pgcon.algo.fitting;

/**
 * @author 段然 2021/12/10
 */
public class ExpTrendLine extends OLSTrendLine {

  @Override
  protected double[] xVector(double x) {
    return new double[]{1, x};
  }

  @Override
  protected boolean logY() {
    return true;
  }
}