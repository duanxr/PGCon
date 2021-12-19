package com.duanxr.pgcon.core.fitting;

import java.util.Arrays;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

/**
 * @author 段然 2021/12/10
 */
public abstract class OLSTrendLine3D  {

  RealMatrix coef = null; // will hold prediction coefs once we get values

  protected abstract double[] xVector(double x); // create vector of values from x

  protected abstract boolean logY(); // set true to predict log of y (note: y must be positive)

  public void setValues(double[] y, double[][] x) {
    if (x.length != y.length) {
      throw new IllegalArgumentException(
          String.format("The numbers of y and x values must be equal (%d != %d)", y.length,
              x.length));
    }
    if (logY()) { // in some models we are predicting ln y, so we replace each y with ln y
      y = Arrays.copyOf(y, y.length); // user might not be finished with the array we were given
      for (int i = 0; i < x.length; i++) {
        y[i] = Math.log(y[i]);
      }
    }
    OLSMultipleLinearRegression ols = new OLSMultipleLinearRegression();
    ols.setNoIntercept(true); // let the implementation include a constant in xVector if desired
    ols.newSampleData(y, x); // provide the data to the model
    coef = MatrixUtils.createColumnRealMatrix(ols.estimateRegressionParameters()); // get our coefs
  }

  public double predict(double x, double z) {
    double yhat = coef.preMultiply(new double[]{x,z})[0]; // apply coefs to xVector
    if (logY()) {
      yhat = (Math.exp(yhat)); // if we predicted ln y, we still need to get y
    }
    return yhat;
  }
}