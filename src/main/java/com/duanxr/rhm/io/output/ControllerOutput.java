package com.duanxr.rhm.io.output;

import java.io.IOException;

/**
 * @author Duanran 2019/12/16
 */
public interface ControllerOutput {

  void output(int command) throws IOException;
}

