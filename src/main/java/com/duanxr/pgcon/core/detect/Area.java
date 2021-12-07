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
public class Area {

  private int x;
  private int y;
  private int high;
  private int weight;
}
