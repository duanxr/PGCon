import com.duanxr.pgcon.core.detect.api.ImageCompare;
import com.duanxr.pgcon.core.model.Area;
import com.duanxr.pgcon.output.action.NintendoSwitchStandardStick;
import com.duanxr.pgcon.output.action.Sticks;
import com.duanxr.pgcon.output.api.Stick;
import com.duanxr.pgcon.script.api.ScriptInfo;

/**
 * @author 段然 2022/8/23
 */
public abstract class MonsterHunterRiseEngine<T> extends NintendoSwitchEngineV2<T> {
  private static final float MONSTER_HUNTER_RISE_ENGINE_SCORE_THRESHOLD = 0.7f;
  private static final ImageCompare.Param WIRE_BUG_READY = ImageCompare.Param.builder()
      .area(Area.ofRect(964, 980, 70, 62)).method(ImageCompare.Method.TM_CCOEFF).preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.builder()
              .enable(true).targetColor(javafx.scene.paint.Color.color(1.0, 1.0, 1.0))
              .range(0.2938144492699917).pickType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.PickType.CIE94)
              .maskType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.MaskType.BLACK)
              .inverse(false).build()).preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.0).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.BINARY)
              .build()).template(
          "{\"R\":62,\"C\":70,\"T\":0,\"D\":\"H/8BAP83TwAAAABDADAPRgAtHgBFAA9CAB8PRQAuHwBFADAGQAAOWwEPUgEcDqEBD0QAXBn/CwEOiQAPRAAbD4sAKB8ARQA3BT8AD0UAKAcJAA7lAQ9FABgIkgAPRAAiD28CLQmJAB//igAULwAAVwECHwCLABoFIgAIyAAIRQAOhQMPvQQHCLgDCOEBDxUBEw9FAAEJgQAOPwMPRgAGC0UACEgAD+QBEg9FAAQFygAPKQITCUoBD44AAA9GAC4f/0UAFg5DAAyOAA8CBBsPjgAED3kDIgw3Ah//RgBaL///RgB8DuwBD0YANx8ARgAmL///jQAwCg8AD8MGJQ9GACwf//AIAQ9GAP9KD4sAvB//RQAzD4sAdQ+KADAf/wEA///WUP//////\",\"L\":4340}")
      .build();

  protected MonsterHunterRiseEngine(ScriptInfo<T> scriptInfo) {
    super(scriptInfo);
  }

  protected void run(int millis, double degree) {
    hold(R);
    walk(millis, new MonsterHunterRiseDirectionStick(degree));
    release(R);
  }

  protected void walk(int millis, Stick stick) {
    hold(stick);
    sleep(millis);
    release(stick);
  }

  protected void walk(int millis, double degree) {
    MonsterHunterRiseDirectionStick stick = new MonsterHunterRiseDirectionStick(degree);
    hold(stick);
    sleep(millis);
    release(stick);
  }

  protected void run(int millis, Stick stick) {
    hold(R);
    walk(millis, stick);
    release(R);
  }

  protected void run(int millis) {
    hold(R);
    walk(millis);
    release(R);
  }

  protected void walk(int millis) {
    hold(L_TOP);
    sleep(millis);
    release(L_TOP);
  }

  protected void turn(double degrees) {
    sleep(150);
    MonsterHunterRiseDirectionStick directionStick = new MonsterHunterRiseDirectionStick(degrees);
    press(directionStick);
    resetCamera();
  }

  protected void resetCamera() {
    sleep(150);
    press(L);
    sleep(350);
  }

  protected void wireBugJump(int dashDelay) {
    wireBugJump();
    sleep(dashDelay);
    press(B);
  }

  protected void wireBugJump() {
    hold(ZL);
    press(X);
    sleep(350);
    release(ZL);
  }

  protected void wireBugJump(int dashDelay, Stick direction) {
    wireBugJump();
    press(direction);
    sleep(dashDelay);
    press(B);
    sleep(150);
    release(direction);
  }

  protected void wireBugDash() {
    hold(ZL);
    press(A);
    sleep(350);
    release(ZL);
  }

  protected void closeAllPanel() {
    until(() -> detect(WIRE_BUG_READY),
        result -> result.getSimilarity() > MONSTER_HUNTER_RISE_ENGINE_SCORE_THRESHOLD, () -> {
          press(B);
          sleep(150);
        });
  }

  protected void checkBugReady() {
    checkBugReady(null, null, null);
  }

  protected void checkBugReady(Runnable action, Long timeout, Runnable reset) {
    until(() -> detect(WIRE_BUG_READY),
        result -> result.getSimilarity() > MONSTER_HUNTER_RISE_ENGINE_SCORE_THRESHOLD,
        () -> {
          if (action != null) {
            action.run();
          }
          sleep(200);
        },
        timeout, reset);
  }

  protected void checkBugReady(Runnable action, Long timeout) {
    checkBugReady(action, timeout, null);
  }

  protected void checkBugReady(Runnable action) {
    checkBugReady(action, null, null);
  }

  private static class MonsterHunterRiseDirectionStick implements Stick {

    private final byte x;
    private final byte y;

    public MonsterHunterRiseDirectionStick(double degrees) {
      int ix = 128;
      int iy = 128;
      ix += 127 * -Math.sin(Math.toRadians(-degrees));
      iy += 127 * -Math.cos(Math.toRadians(-degrees));
      this.x = (byte) ix;
      this.y = (byte) iy;
    }

    @Override
    public Sticks getStick() {
      return Sticks.LEFT;
    }

    @Override
    public byte getXCommandPGCon() {
      return NintendoSwitchStandardStick.L_CENTER.getXCommandPGCon();
    }

    @Override
    public byte getYCommandPGCon() {
      return NintendoSwitchStandardStick.L_CENTER.getYCommandPGCon();
    }

    @Override
    public byte getXCommandEasyCon() {
      return x;
    }

    @Override
    public byte getYCommandEasyCon() {
      return y;
    }
  }

}
