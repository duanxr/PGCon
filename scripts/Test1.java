import com.duanxr.pgcon.core.detect.api.ImageCompare;
import com.duanxr.pgcon.core.model.Area;
import com.duanxr.pgcon.exception.ResetScriptException;
import com.duanxr.pgcon.output.action.StickAction;
import com.duanxr.pgcon.script.api.ScriptInfo;
import com.duanxr.pgcon.script.engine.PGConScriptEngineV1;

/**
 * @author DuanXR 2021/12/9
 */
public class Test1 extends PGConScriptEngineV1<Object> {

  private static final ImageCompare.Param FOUND_GREAT_BUG = ImageCompare.Param.builder()
      .area(Area.ofRect(456, 254, 1078, 624))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.builder()
              .enable(true)
              .targetColor(javafx.scene.paint.Color.color(1.0, 1.0, 0.0))
              .range(0.3505154055125795)
              .pickType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.PickType.CIEDE2000)
              .maskType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.MaskType.BLACK)
              .inverse(true)
              .build())
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .binaryThreshold(0.0)
          .inverse(false)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.BINARY)
          .build())
      .template(
          "{\"R\":36,\"C\":180,\"T\":0,\"D\":\"HwABAP/////jT/////9aAEQPsQBHL///twAqBjwABgoAARUAD1UACi///7MALw9EABov//+0AAEHRgAe/7QADukADxcCLQ61AA+0ABYfALQAAw4cAg+0ADYPRQAhCywABA8ADbQAD/cACw+zAC4OZwAOIAAIlAEv//+0AAQX/xIAH//YAQoPuQAMH/+sAQIf/5cCAAOIAh3/nAAH9QIHCwAPtAAKBpgABIAADUQABUgACBUDB00ADWcDDlQCBpUDCzUABmMAD7QABwM2AA60AA9SAAwe/7QADhwED7QADwadAgYiAQOXAAccAA+jAQAFtAANfwAJPQAKYAAHYQAIYgAOaQAPpQQJCHoCCnkCB54ADEkACCkADLQAB0oADUQAD7QAAg7PBA+0AAIKYgAFtAAJjgEERAMOZgILbQEPtAAACBsDCfgAB/IABLQABA4ALwAAtAAUCgMDB1ICL///tAAkD1IAAQtmBQy0AB8AtAANCkkCD7QAAgwyAw+0AAUItQcPtAAhHwC0AA8NZgUPYgABCkgDD7QAAxv/OQAPUgAQD7QACh//tAAkDioAD7QAFg9SAAMXAO8JDtACD7QAEgmZAB//tAAMHwC0ABQfALQAEA84BBoKagAMJAke/5AGDiIDD7QAJggJAA4DBQ20AC//AKAFAgZ9Ai///7QAAQ7CBgiSAw5nAg5UCA9SABMO7gIPJAsLD44AAA0aAA5EAA2gBg60AA8ICQAv//9xCAsPHwAJHwBhCAMHuQEO8gEHbAcfAO4EBQhjAwdLAC//AFIAAy8AAA4AAQthAw5gCA/RDQ8PAQBtDrMADwEAhg44BA8MEJYf/7QAnQ6CAw+1AJQfAAEA///JUAAAAAAA\",\"L\":6480}")
      .build();
  private static final ImageCompare.Param FOUND_RESOURCES = ImageCompare.Param.builder()
      .area(Area.ofRect(456, 254, 1078, 624))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.builder()
              .enable(true)
              .targetColor(javafx.scene.paint.Color.color(1.0, 1.0, 1.0))
              .range(0.2938144370827872)
              .pickType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.PickType.CIE94)
              .maskType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.MaskType.BLACK)
              .inverse(true)
              .build())
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .binaryThreshold(0.0)
          .inverse(false)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.BINARY)
          .build())
      .template(
          "{\"R\":40,\"C\":38,\"T\":0,\"D\":\"HwABAN9P/////yMAEAIBAA8kABEOKAAOJQAOKAAOJAAPJwAKCBsACAwADyUABgYOAA8lAAoOTQAOJgAOJwAPJgAaCRUAH/8mABEu//9NAA8HAQIPJgAMB+8BDyYACz///wAmAAIPEwAdDx0BAB8AJgAeAhMACzAADyYABwwaAg7vAQ6FAQ8mAAQJggIOJQAOrAIP0wIWDiUADygAAC8AACcAAw8lABIO8QIPAQD8UAAAAAAA\",\"L\":1520}")
      .build();
  private static final ImageCompare.Param IN_CAVERNS = ImageCompare.Param.builder()
      .area(Area.ofRect(964, 980, 70, 62))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.builder()
              .enable(true)
              .targetColor(javafx.scene.paint.Color.color(1.0, 1.0, 1.0))
              .range(0.2938144492699917)
              .pickType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.PickType.CIE94)
              .maskType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.MaskType.BLACK)
              .inverse(false)
              .build())
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .binaryThreshold(0.0)
          .inverse(false)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.BINARY)
          .build())
      .template(
          "{\"R\":62,\"C\":70,\"T\":0,\"D\":\"H/8BAP83TwAAAABDADAPRgAtHgBFAA9CAB8PRQAuHwBFADAGQAAOWwEPUgEcDqEBD0QAXBn/CwEOiQAPRAAbD4sAKB8ARQA3BT8AD0UAKAcJAA7lAQ9FABgIkgAPRAAiD28CLQmJAB//igAULwAAVwECHwCLABoFIgAIyAAIRQAOhQMPvQQHCLgDCOEBDxUBEw9FAAEJgQAOPwMPRgAGC0UACEgAD+QBEg9FAAQFygAPKQITCUoBD44AAA9GAC4f/0UAFg5DAAyOAA8CBBsPjgAED3kDIgw3Ah//RgBaL///RgB8DuwBD0YANx8ARgAmL///jQAwCg8AD8MGJQ9GACwf//AIAQ9GAP9KD4sAvB//RQAzD4sAdQ+KADAf/wEA///WUP//////\",\"L\":4340}")
      .build();
  private static final ImageCompare.Param TRAVEL_READ = ImageCompare.Param.builder()
      .area(Area.ofRect(818, 534, 282, 40))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(false)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template(
          "{\"R\":40,\"C\":282,\"T\":0,\"D\":\"HwABAP/////////FGv8BAA9BACEPcgAfDg4AD9oARw8aAWYKNQAO6AAOEwEPGgFUL///GwEtDxoBxALOAA4bAQ/gACYPGgFeHwAzAkgO2QAPGgFRL///OgAAH/8aARcPbwATH/8OABYPbQAEHwAaATgEhQIGaAMIYAAGigED3QQIDQELFwAKngMFEQUEVwAFKQAPLwUFCUQACo0ABQ4ADnIAC5cADxoBNwx1AAXOAASCAQjcAAqvAi////4ABArbAAZYAAV0AQ8vAAALFQAf/10AAg8iAAMDHAEfABoBRRz/jQAFKAAPGgACBz0ACrwADzQCCAkaAR4ASgEPGgEKCqoAD3QAAw5oBA8aAS4MqQMJIQIJQQINiQADSwQNawMPHQAEDl8AB2YBCY0BCpoBBX8GKwAAowEM4AMfABoBOR0AwwQPGgEaCk4AD5wGBg0aAQ+SAAYOzgMOYwEPGgEBH/8aAT0JFwALmAEbAMYCDxoBJw9TAAAPLgcKDnMADNcCCmgADjMCDxoBNQ3oAw8aATwI0QAOsQoPjwUGC1sDCpcDChoBDhkBDxoBMgzrBQ3NBw8aATkONwIKIgQf/xoBJR8AGgFECtAACVkDBwIED04DKQwVBB//kgABC20FDxoBXR8APQoEDosJDxoBFw6cBg9oBAYMMAAKogEODwYPggUIDxoBRAzfBARkBwfTAQ5QBw+2Bx0HEAAPGgEFCMoHGv8NAA+cBgcLdQAOTgMPaAQ9BjUCDpwFHv8QBAzNCQ4dAA+2BwEf/+oJBw9oBBULRQAOtAcPGgEqDRMFCX4LCxoBHgAaAQ9sBgUGdgIf/5wGEg5mAAyRAA8aAQMPzgg7AuYED/QGAQvrAi4AAC4FC30AClkBD7UDAghbBQ9SDhAOVwENkgAEFQADMQwMMwUPAQDkDwcBCw8aAf////9DDt8ZDwEA/////////////lAAAAAAAA==\",\"L\":11280}")
      .build();

  public Test1() {
    super(ScriptInfo.builder()
        .description("Test1")
        .build());
  }

  @Override
  public void execute() {
    checkFreedom();
    sleep(5000);
    tryGet(this::get1);
    tryGet(this::get8);
    tryGet(this::get7);
    tryGet(this::get5);
    tryGet(this::get3);
  }

  private void tryGet(Runnable runnable) {
    try {
      runnable.run();
    } catch (ResetScriptException e) {
      error("not found!");
    }
  }

  private void checkFreedom() {
    until(() -> detect(IN_CAVERNS),
        result -> result.getSimilarity() > 0.9,
        () -> sleep(50));//todo add stuck check
    press(L);
    sleep(350);
  }

  private void get8() {
    fastTravel(2);

    turn(470);
    sleep(350);
    walk(8950);

    turn(340);
    sleep(350);
    walk(10050);

    getResources();


  }

  private void get7() {
    fastTravel(0);

    turn(620);
    sleep(350);
    run(12550);

    turn(-320);
    sleep(350);
    walk(80);
    sleep(150);

    run(1200);

    turn(-120);
    sleep(350);
    press(L);
    walk(1350);

    getResources();

  }

  private void fastTravel(int camp) {
    press(MINUS);
    sleep(1250);
    press(A);
    sleep(350);
    for (int i = 0; i < camp; i++) {
      press(D_BOTTOM);
      sleep(350);
    }
    press(A);
    sleep(350);
    until(() -> detect(TRAVEL_READ),
        result -> result.getSimilarity() > 0.9,
        () -> sleep(50));//todo add stuck check
    press(A);
    sleep(150);
    checkFreedom();
    sleep(1000);
  }

  private void run(int millis) {
    hold(R);
    walk(millis);
    release(R);
  }

  private void get5() {
    fastTravel(1);

    turn(780);
    sleep(350);
    walk(12850);

    turn(280);
    sleep(350);
    walk(3150);

    useGreatBug();

    press(L);
    sleep(350);

    walk(5000);
    sleep(350);

    turn(260);
    sleep(350);
    walk(6150);

    getResources();

  }

  private void useGreatBug() {
    until(() -> detect(FOUND_GREAT_BUG),
        result -> result.getSimilarity() > 0.75,
        () -> sleep(50),2000L);//todo add stuck check
    error("found !");//todo a
    press(A);
    sleep(7000);
  }

  private void get6() {
    turn(-280);
    sleep(150);
    walk(3550);
  }

  private void turn(int millis) {
    StickAction stickAction = millis < 0 ? StickAction.R_LEFT : StickAction.R_RIGHT;
    hold(stickAction);
    sleep(Math.abs(millis));
    release(stickAction);
  }

  private void walk(int millis) {
    hold(StickAction.L_TOP);
    sleep(millis);
    release(StickAction.L_TOP);
  }

  private void get4() {

    turn(80);
    walk(150);

    sleep(350);
    bugJump();
    sleep(150);
    bugJump();

    turn(-200);
    sleep(150);
    walk(6250);
    getResources();

  }

  private void bugJump() {
    hold(ZL);
    press(X);
    sleep(800);
    press(B);
    release(ZL);
  }

  private void getResources() {
    until(() -> detect(FOUND_RESOURCES),
        result -> result.getSimilarity() > 0.75,
        () -> sleep(50),
        2000L);//todo add stuck check
    warn("found!");//todo a
  }

  private void get3() {
    fastTravel(2);

    turn(-470);
    sleep(350);
    walk(3550);

    turn(380);
    sleep(350);
    walk(9550);

    turn(170);
    sleep(350);
    walk(5450);

    getResources();

    //get4();
  }

  private void get1() {
    fastTravel(1);

    turn(780);
    sleep(350);
    walk(2550);

    getResources();

    get2();

  }

  private void get2() {
    turn(460);
    sleep(350);
    walk(7350);

    getResources();
  }


}
