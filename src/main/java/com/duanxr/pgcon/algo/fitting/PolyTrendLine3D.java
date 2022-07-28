package com.duanxr.pgcon.algo.fitting;

/**
 * @author 段然 2021/12/10
 */
public class PolyTrendLine3D extends OLSTrendLine3D {

  final int degree;

  public PolyTrendLine3D(int degree) {
    if (degree < 0) {
      throw new IllegalArgumentException("The degree of the polynomial must not be negative");
    }
    this.degree = degree;
  }

  protected double[] xVector(double x) { // {1, x, x*x, x*x*x, ...}
    double[] poly = new double[degree + 1];
    double xi = 1;
    for (int i = 0; i <= degree; i++) {
      poly[i] = xi;
      xi *= x;
    }
    return poly;
  }

  @Override
  protected boolean logY() {
    return false;
  }
}