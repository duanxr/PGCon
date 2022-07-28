package com.duanxr.pgcon.script.api;

import java.io.Serializable;
import java.util.Map;
import javafx.beans.property.Property;

/**
 * @author 段然 2021/12/9
 */
public interface DynamicScript extends MainScript {

  Serializable registerConfigBean();

}
