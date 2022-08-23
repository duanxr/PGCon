package com.duanxr.pgcon.script.component;

import com.duanxr.pgcon.exception.LoadScriptException;
import com.google.common.base.Strings;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.openhft.compiler.CompilerUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/8/23
 */
@Slf4j
@Component
public class ScriptCompiler {

  @SneakyThrows
  public Class<?> compile(File file) {
    return compile(this.getClass().getClassLoader(), file);
  }

  @SneakyThrows
  public Class<?> compile(ClassLoader classLoader, File file) {
    String code = null;
    try {
      code = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
    } catch (Exception e) {
      throw new LoadScriptException("load script file error", e);
    }
    String fileName = file.getName();
    String packageName = findPackageName(code);
    String className = fileName.substring(0, fileName.lastIndexOf("."));
    if (!Strings.isNullOrEmpty(packageName)) {
      className = packageName + "." + className;
    }
    try {
      return CompilerUtils.CACHED_COMPILER.loadFromJava(classLoader, className, code);
    } catch (ClassNotFoundException e) {
      throw new LoadScriptException(
          "can't found class " + className
              + "in script file , please make sure the class name is the same as the script file name",
          e);
    } catch (Exception e) {
      throw new LoadScriptException("compile script file " + file.getAbsolutePath() + " error", e);
    }
  }

  private String findPackageName(String code) {
    try {
      int index = code.indexOf("package");
      if (index == -1) {
        return null;
      }
      int endIndex = code.indexOf(";", index);
      if (endIndex == -1) {
        return null;
      }
      return code.substring(index + 8, endIndex).trim();
    } catch (Exception e) {
      log.error("find package name error", e);
      return null;
    }

  }

  @SuppressWarnings("unchecked")
  public boolean isJDK() {
    try {
      String className = "PGConJdkTest";
      String javaCode = """
          public class PGConJdkTest implements Supplier<Boolean> {
                @Override
                public Boolean get() {
                  return true;
                }
          }
          """;
      Class<Supplier<Boolean>> jdkTestClass = CompilerUtils.CACHED_COMPILER.loadFromJava(
          className, javaCode);
      Supplier<Boolean> jdkTest = jdkTestClass.getDeclaredConstructor().newInstance();
      return jdkTest.get();
    } catch (Exception e) {
      return false;
    }
  }
}
