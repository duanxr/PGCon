package com.duanxr.pgcon.core.detect;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 段然 2021/12/6
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetectResult<R> {

  private boolean isDetected;

  private long timeStamp;

  private R result;
}
