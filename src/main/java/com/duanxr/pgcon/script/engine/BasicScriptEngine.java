package com.duanxr.pgcon.script.engine;

import com.duanxr.pgcon.component.PGConComponents;
import com.duanxr.pgcon.output.action.ButtonAction;
import com.duanxr.pgcon.output.action.StickAction;
import com.duanxr.pgcon.script.api.Script;
import com.duanxr.pgcon.script.api.ScriptInfo;

/**
 * @author 段然 2022/8/16
 */
public abstract class BasicScriptEngine<T> implements Script<T> {
  protected static ButtonAction A = ButtonAction.A;
  protected static ButtonAction B = ButtonAction.B;
  protected static ButtonAction X = ButtonAction.X;
  protected static ButtonAction Y = ButtonAction.Y;
  protected static ButtonAction L = ButtonAction.L;
  protected static ButtonAction R = ButtonAction.R;
  protected static ButtonAction ZL = ButtonAction.ZL;
  protected static ButtonAction ZR = ButtonAction.ZR;
  protected static ButtonAction L_STICK = ButtonAction.L_STICK;
  protected static ButtonAction R_STICK = ButtonAction.R_STICK;
  protected static ButtonAction D_TOP = ButtonAction.D_TOP;
  protected static ButtonAction D_BOTTOM = ButtonAction.D_BOTTOM;
  protected static ButtonAction D_LEFT = ButtonAction.D_LEFT;
  protected static ButtonAction D_RIGHT = ButtonAction.D_RIGHT;
  protected static ButtonAction PLUS = ButtonAction.PLUS;
  protected static ButtonAction MINUS = ButtonAction.MINUS;
  protected static ButtonAction CAPTURE = ButtonAction.CAPTURE;
  protected static ButtonAction HOME = ButtonAction.HOME;
  protected static StickAction L_TOP = StickAction.L_TOP;
  protected static StickAction L_BOTTOM = StickAction.L_BOTTOM;
  protected static StickAction L_LEFT = StickAction.L_LEFT;
  protected static StickAction L_RIGHT = StickAction.L_RIGHT;
  protected static StickAction R_TOP = StickAction.R_TOP;
  protected static StickAction R_BOTTOM = StickAction.R_BOTTOM;
  protected static StickAction R_LEFT = StickAction.R_LEFT;
  protected static StickAction R_RIGHT = StickAction.R_RIGHT;
  protected static StickAction L_TOP_RIGHT = StickAction.L_TOP_RIGHT;
  protected static StickAction L_BOTTOM_RIGHT = StickAction.L_BOTTOM_RIGHT;
  protected static StickAction L_BOTTOM_LEFT = StickAction.L_BOTTOM_LEFT;
  protected static StickAction L_TOP_LEFT = StickAction.L_TOP_LEFT;
  protected static StickAction R_TOP_RIGHT = StickAction.R_TOP_RIGHT;
  protected static StickAction R_BOTTOM_RIGHT = StickAction.R_BOTTOM_RIGHT;
  protected static StickAction R_BOTTOM_LEFT = StickAction.R_BOTTOM_LEFT;
  protected static StickAction R_TOP_LEFT = StickAction.R_TOP_LEFT;
  protected static StickAction L_CENTER = StickAction.L_CENTER;
  protected static StickAction R_CENTER = StickAction.R_CENTER;
  protected final T config;
  private final ScriptInfo<T> scriptInfo;
  protected PGConComponents components;


  protected BasicScriptEngine(ScriptInfo<T> scriptInfo) {
    this.scriptInfo = scriptInfo;
    this.config = scriptInfo.getConfig();
  }
  @Override
  public ScriptInfo<T> getInfo() {
    return this.scriptInfo;
  }

  @Override
  public void setComponents(PGConComponents components) {
    this.components = components;
  }
}
