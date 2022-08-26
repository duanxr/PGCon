package com.duanxr.pgcon.util;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.ComboBox;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;

/**
 * @author 段然 2022/8/14
 */
@Slf4j
@UtilityClass
public class PropertyCacheUtil {

  public static void bindBooleanProperty(String key, BooleanProperty property) {
    loadProperty(key, property, Boolean::valueOf);
    bindProperty(key, property, Object::toString);
  }

  public static <T> void loadProperty(String key, Property<T> property,
      Function<String, T> converter) {
    CallBackUtil.callbackWithExceptionCatch(() -> {
      String cache = ConfigCacheUtil.get(key);
      if (!Strings.isNullOrEmpty(cache)) {
        T value = deserialize(cache, converter);
        if (value != null) {
          property.setValue(value);
        }
      }
    });
  }

  public static <T> void bindProperty(String key, Property<T> property,
      Function<T, String> converter) {
    property.addListener((observable, oldValue, newValue) ->
        CallBackUtil.callbackWithExceptionCatch(() -> {
          String cache = serialize(newValue, converter);
          if (!Strings.isNullOrEmpty(cache)) {
            ConfigCacheUtil.set(key, cache);
          }
        }));
  }

  private static <T> T deserialize(String serialized, Function<String, T> converter) {
    try {
      return converter.apply(serialized);
    } catch (Exception e) {
      log.error("Cannot deserialize property", e);
      return null;
    }
  }

  private static <T> String serialize(T property, Function<T, String> converter) {
    try {
      return converter.apply(property);
    } catch (Exception e) {
      log.error("Cannot serialize property", e);
      return null;
    }
  }

  public static void bindStringProperty(String key, StringProperty property) {
    loadProperty(key, property, Function.identity());
    bindProperty(key, property, Function.identity());
  }

  public static void bindDoubleProperty(String key, DoubleProperty property) {
    loadProperty(key, property, Double::parseDouble);
    bindProperty(key, property, input -> String.valueOf(input.doubleValue()));
  }

  public static void bindFloatProperty(String key, FloatProperty property) {
    loadProperty(key, property, Float::parseFloat);
    bindProperty(key, property, input -> String.valueOf(input.floatValue()));
  }

  public static void bindIntegerProperty(String key, IntegerProperty property) {
    loadProperty(key, property, Integer::parseInt);
    bindProperty(key, property, input -> String.valueOf(input.intValue()));
  }

  public static void bindLongProperty(String key, LongProperty property) {
    loadProperty(key, property, Long::parseLong);
    bindProperty(key, property, input -> String.valueOf(input.longValue()));
  }

  public static <T extends Enum<T>> void bindEnumComboBoxProperty(String key,
      ComboBox<T> comboBox, Class<T> enumClass) {
    loadProperty(key, comboBox.valueProperty(), input -> T.valueOf(enumClass, input));
    bindProperty(key, comboBox.valueProperty(), Enum::name);
  }

  public static void bindStringsComboBoxProperty(String key,
      ComboBox<String> comboBox, Collection<String> collection) {
    loadProperty(key, comboBox.valueProperty(), input -> collection.contains(input) ? input : null);
    bindProperty(key, comboBox.valueProperty(), Function.identity());
  }

  public static void bindPropertyBean(String key, Object bean) {
    String cache = ConfigCacheUtil.get(key);
    deserialize(bean, cache);
    List<Property<?>> properties = getBeanPropertiesWithGetter(bean);
    ChangeListener<Object> changeListener = (observable, oldValue, newValue) -> CallBackUtil.callbackWithExceptionCatch(
        () -> ConfigCacheUtil.set(key, serialize(bean)));
    for (Property<?> property : properties) {
      property.addListener(changeListener);
    }
  }

  private static <T> void deserialize(T bean, String cache) {
    try {
      if (!Strings.isNullOrEmpty(cache)) {
        byte[] decode = Base64.getDecoder().decode(cache);
        T cachedBean = JSONObject.parseObject(decode, bean.getClass());
        BeanUtils.copyProperties(bean, cachedBean);
      }
    } catch (Exception e) {
      log.error("Cannot deserialize bean", e);
    }
  }

  private static List<Property<?>> getBeanPropertiesWithGetter(Object bean) {
    return Arrays.stream(bean.getClass().getDeclaredMethods())
        .filter(method -> method.getParameterCount() == 0)
        .filter(method -> method.getName().startsWith("get"))
        .filter(method -> Property.class.isAssignableFrom(method.getReturnType()))
        .map(method -> {
          try {
            return (Property<?>) method.invoke(bean);
          } catch (Exception e) {
            log.error("Cannot invoke method", e);
            return null;
          }
        })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private static String serialize(Object bean) {
    try {
      String json = JSONObject.toJSONString(bean);
      byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);
      return Base64.getEncoder().encodeToString(jsonBytes);
    } catch (Exception e) {
      log.error("Cannot serialize bean", e);
      return null;
    }
  }


}
