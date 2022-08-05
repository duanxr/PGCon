package com.duanxr.pgcon.algo.fitting.impl;

/**
 * @author 段然 2021/12/10
 */
public class PowerTrendLine extends OLSTrendLine {

  @Override
  protected double[] xVector(double x) {
    return new double[]{1, Math.log(x)};
  }

  @Override
  protected boolean logY() {
    return true;
  }

}