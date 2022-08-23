package com.duanxr.pgcon.script.engine;

import com.duanxr.pgcon.component.PGConComponents;
import com.duanxr.pgcon.output.action.NintendoSwitchStandardButton;
import com.duanxr.pgcon.output.action.NintendoSwitchStandardStick;
import com.duanxr.pgcon.script.api.Script;
import com.duanxr.pgcon.script.api.ScriptInfo;

/**
 * @author 段然 2022/8/16
 */
public abstract class BasicScriptEngine<T> implements Script<T> {
  protected static NintendoSwitchStandardButton A = NintendoSwitchStandardButton.A;
  protected static NintendoSwitchStandardButton B = NintendoSwitchStandardButton.B;
  protected static NintendoSwitchStandardButton X = NintendoSwitchStandardButton.X;
  protected static NintendoSwitchStandardButton Y = NintendoSwitchStandardButton.Y;
  protected static NintendoSwitchStandardButton L = NintendoSwitchStandardButton.L;
  protected static NintendoSwitchStandardButton R = NintendoSwitchStandardButton.R;
  protected static NintendoSwitchStandardButton ZL = NintendoSwitchStandardButton.ZL;
  protected static NintendoSwitchStandardButton ZR = NintendoSwitchStandardButton.ZR;
  protected static NintendoSwitchStandardButton L_STICK = NintendoSwitchStandardButton.L_STICK;
  protected static NintendoSwitchStandardButton R_STICK = NintendoSwitchStandardButton.R_STICK;
  protected static NintendoSwitchStandardButton D_TOP = NintendoSwitchStandardButton.D_TOP;
  protected static NintendoSwitchStandardButton D_BOTTOM = NintendoSwitchStandardButton.D_BOTTOM;
  protected static NintendoSwitchStandardButton D_LEFT = NintendoSwitchStandardButton.D_LEFT;
  protected static NintendoSwitchStandardButton D_RIGHT = NintendoSwitchStandardButton.D_RIGHT;
  protected static NintendoSwitchStandardButton PLUS = NintendoSwitchStandardButton.PLUS;
  protected static NintendoSwitchStandardButton MINUS = NintendoSwitchStandardButton.MINUS;
  protected static NintendoSwitchStandardButton CAPTURE = NintendoSwitchStandardButton.CAPTURE;
  protected static NintendoSwitchStandardButton HOME = NintendoSwitchStandardButton.HOME;
  protected static NintendoSwitchStandardStick L_TOP = NintendoSwitchStandardStick.L_TOP;
  protected static NintendoSwitchStandardStick L_BOTTOM = NintendoSwitchStandardStick.L_BOTTOM;
  protected static NintendoSwitchStandardStick L_LEFT = NintendoSwitchStandardStick.L_LEFT;
  protected static NintendoSwitchStandardStick L_RIGHT = NintendoSwitchStandardStick.L_RIGHT;
  protected static NintendoSwitchStandardStick R_TOP = NintendoSwitchStandardStick.R_TOP;
  protected static NintendoSwitchStandardStick R_BOTTOM = NintendoSwitchStandardStick.R_BOTTOM;
  protected static NintendoSwitchStandardStick R_LEFT = NintendoSwitchStandardStick.R_LEFT;
  protected static NintendoSwitchStandardStick R_RIGHT = NintendoSwitchStandardStick.R_RIGHT;
  protected static NintendoSwitchStandardStick L_TOP_RIGHT = NintendoSwitchStandardStick.L_TOP_RIGHT;
  protected static NintendoSwitchStandardStick L_BOTTOM_RIGHT = NintendoSwitchStandardStick.L_BOTTOM_RIGHT;
  protected static NintendoSwitchStandardStick L_BOTTOM_LEFT = NintendoSwitchStandardStick.L_BOTTOM_LEFT;
  protected static NintendoSwitchStandardStick L_TOP_LEFT = NintendoSwitchStandardStick.L_TOP_LEFT;
  protected static NintendoSwitchStandardStick R_TOP_RIGHT = NintendoSwitchStandardStick.R_TOP_RIGHT;
  protected static NintendoSwitchStandardStick R_BOTTOM_RIGHT = NintendoSwitchStandardStick.R_BOTTOM_RIGHT;
  protected static NintendoSwitchStandardStick R_BOTTOM_LEFT = NintendoSwitchStandardStick.R_BOTTOM_LEFT;
  protected static NintendoSwitchStandardStick R_TOP_LEFT = NintendoSwitchStandardStick.R_TOP_LEFT;
  protected static NintendoSwitchStandardStick L_CENTER = NintendoSwitchStandardStick.L_CENTER;
  protected static NintendoSwitchStandardStick R_CENTER = NintendoSwitchStandardStick.R_CENTER;
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
