package com.duanxr.rhm.script;

import com.duanxr.rhm.core.execute.ScriptExecutor;
import java.util.List;

/**
 * @author Duanran 2019/12/19
 */
public interface Script {

  List<Subscript> getSubscript();

  void setExecutor(ScriptExecutor scriptExecutor);
}
