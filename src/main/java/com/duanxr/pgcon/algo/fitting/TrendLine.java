package com.duanxr.pgcon.algo.fitting;

/**
 * @author 段然 2021/12/10
 */
public interface TrendLine {

  void setValues(double[] y, double[] x); // y ~ f(x)


  double predict(double x);// get a predicted y for a given x

}
