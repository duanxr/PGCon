package com.duanxr.pgcon.test;

import com.duanxr.pgcon.script.api.Script;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;
import net.openhft.compiler.CompilerUtils;
import org.apache.commons.io.FileUtils;

/**
 * @author 段然 2022/8/16
 */
public class ScriptLoadTest {

  @SneakyThrows
  public static void main(String[] args) {
    String className = "Test1";
    String javaCode = FileUtils.readFileToString(new java.io.File("script/Test1.java"),
        StandardCharsets.UTF_8);
    Class<?> aClass = CompilerUtils.CACHED_COMPILER.loadFromJava(className, javaCode);
    Constructor<?> declaredConstructor = aClass.getDeclaredConstructor();
    Object newInstance = declaredConstructor.newInstance();
    Runnable runner = (Runnable) newInstance;
    runner.run();
    Script newInstance1 = (Script) newInstance;
    System.out.printf("%s", newInstance1.getInfo().getDescription());
    newInstance1.execute();
  }
}
