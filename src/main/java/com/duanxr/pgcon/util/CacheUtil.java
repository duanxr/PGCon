package com.duanxr.pgcon.util;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.function.Function;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ComboBox;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;

/**
 * @author Duanran 2019/7/2 0002
 */
@Slf4j
@UtilityClass
public class CacheUtil {

  private static final String CACHE_CONFIG_NAME = "PGConCache.ini";
  private static final Configuration CONFIG = loadConfig();

  @SneakyThrows
  private static Configuration loadConfig() {
    try {
      File configFile = new File(CACHE_CONFIG_NAME);
      if (!configFile.exists()) {
        configFile.createNewFile();
      }
      FileBasedConfigurationBuilder<PropertiesConfiguration> builder = new Configurations().propertiesBuilder(
          configFile);
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

  public static void bindCache(String key, BooleanProperty booleanProperty) {
    loadCache(key, booleanProperty);
    booleanProperty.addListener(
        (observable, oldValue, newValue) -> {
          if (newValue != null) {
            set(key, newValue.toString());
          }
        });
  }

  public static void loadCache(String key, BooleanProperty booleanProperty) {
    booleanProperty.setValue(Boolean.TRUE.toString().equalsIgnoreCase(CacheUtil.get(key)));
  }


  public static void bindCache(String key, StringProperty stringProperty,
      Function<String, Boolean> validator) {
    loadCache(key, stringProperty, validator);
    stringProperty.addListener(
        (observable, oldValue, newValue) -> {
          if (newValue != null && validator.apply(newValue)) {
            set(key, newValue);
          }
        });
  }

  public static void loadCache(String key, StringProperty stringProperty,
      Function<String, Boolean> validator) {
    String cache = CacheUtil.get(key);
    if (validator.apply(cache)) {
      stringProperty.setValue(cache);
    }
  }

  public static <O> void bindCache(String key, ComboBox<O> comboBox,
      Function<O, String> serializer, Function<String, O> deserializer) {
    loadCache(key, comboBox, deserializer);
    comboBox.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> {
          if (newValue != null) {
            String cache = serializer.apply(newValue);
            if (!Strings.isNullOrEmpty(cache)) {
              CacheUtil.set(key, cache);
            }
          }
        });
  }

  public static <O> void loadCache(String key, ComboBox<O> comboBox,
      Function<String, O> deserializer) {
    String cache = CacheUtil.get(key);
    if (!Strings.isNullOrEmpty(cache)) {
      O cacheObj = deserializer.apply(cache);
      if (cacheObj != null) {
        comboBox.getSelectionModel().select(cacheObj);
      }
    }
  }

  @SneakyThrows
  public static <B> B loadCache(String key, B dest) {
    String cache = CacheUtil.get(key);
    if (!Strings.isNullOrEmpty(cache)) {
      byte[] decode = Base64.getDecoder().decode(cache);
      B cachedBean = JSONObject.parseObject(decode, dest.getClass());
      BeanUtils.copyProperties(dest, cachedBean);
    }
    return dest;
  }

  @SneakyThrows
  public static <B> void setCache(String key, B dest) {
    String cache = JSONObject.toJSONString(dest);
    String encode = Base64.getEncoder().encodeToString(cache.getBytes(StandardCharsets.UTF_8));
    CacheUtil.set(key, encode);
  }


}
