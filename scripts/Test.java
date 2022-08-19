import com.duanxr.pgcon.script.api.ScriptInfo;
import com.duanxr.pgcon.script.engine.PGConScriptEngineV1;

/**
 * @author DuanXR 2021/12/9
 */
public class Test extends PGConScriptEngineV1<Object> {

  public Test() {
    super(ScriptInfo.builder()
        .name("Test")
        .build());
  }

  @Override
  public void execute() {
    press(A);
    sleep(150);
    press(B);
    sleep(150);
    press(X);
    sleep(150);
    press(Y);
    sleep(150);
    press(L);
    sleep(150);
    press(R);
    sleep(150);
    press(ZL);
    sleep(150);
    press(ZR);
    sleep(150);
    press(L_STICK);
    sleep(150);
    press(R_STICK);
    sleep(150);
    press(D_TOP);
    sleep(150);
    press(D_BOTTOM);
    sleep(150);
    press(D_RIGHT);
    sleep(150);
    press(D_LEFT);
    sleep(150);
    press(PLUS);
    sleep(150);
    press(MINUS);
    sleep(150);
    press(CAPTURE);
    sleep(5000);
    press(HOME);
  }


}
