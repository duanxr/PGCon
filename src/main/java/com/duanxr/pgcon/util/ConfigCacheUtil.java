package com.duanxr.pgcon.util;

import static com.duanxr.pgcon.config.ConstantConfig.CACHE_CONFIG_NAME;

import java.io.File;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;

/**
 * @author Duanran 2019/7/2 0002
 */
@Slf4j
@UtilityClass
public class ConfigCacheUtil {
  private static final Configuration CONFIG = loadConfig();
  @SneakyThrows
  private static Configuration loadConfig() {
    try {
      File configFile = new File(CACHE_CONFIG_NAME);
      if (!configFile.exists()) {
        boolean created = configFile.createNewFile();
        if (!created) {
          log.warn("Cannot create config file");
        }
      }
      FileBasedConfigurationBuilder<PropertiesConfiguration> builder =
          new Configurations().propertiesBuilder(configFile);
      builder.setAutoSave(true);
      return builder.getConfiguration();
    } catch (Exception e) {
      log.info("Cannot load or create config file", e);
    }
    return null;
  }
  public static String get(String key) {
    return CONFIG == null ? "" : CONFIG.getString(key, "");
  }
  public static void set(String key, String value) {
    if (CONFIG != null) {
      CONFIG.setProperty(key, value);
    }
  }

}
