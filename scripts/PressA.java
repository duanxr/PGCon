import com.duanxr.pgcon.script.engine.PGConScriptEngineV1;

/**
 * @author 段然 2021/12/9
 */
public class PressA extends PGConScriptEngineV1<Object> {
  public PressA() {
    super(ScriptInfo.builder()
        .isLoop(true)
        .name("Press A")
        .build());
  }

  @Override
  public void execute() {
    press(A);
    sleep(150);
  }

}
