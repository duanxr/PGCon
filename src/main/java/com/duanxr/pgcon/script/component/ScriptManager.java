package com.duanxr.pgcon.script.component;

import com.duanxr.pgcon.script.api.Script;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/8/19
 */
@Component
public class ScriptManager {

  private final Map<String, ScriptCache<Object>> nameMap;
  private final Map<String, ScriptCache<Object>> descriptionMap;
  private final Map<Class<? extends Script<Object>>, ScriptCache<Object>> classMap;
  public ScriptManager() {
    this.descriptionMap = new ConcurrentHashMap<>();
    this.nameMap = new ConcurrentHashMap<>();
    this.classMap = new ConcurrentHashMap<>();
  }
  public ScriptCache<Object> getScriptByDescription(String scriptName) {
    return descriptionMap.get(scriptName);
  }
  public ScriptCache<Object> getScriptByName(String scriptName) {
    return nameMap.get(scriptName);
  }
  public ScriptCache<Object> getScriptByClass(Class<? extends Script<Object>> scriptClass) {
    return classMap.get(scriptClass);
  }
  public Collection<ScriptCache<Object>> getScripts() {
    return descriptionMap.values();
  }
  @SuppressWarnings("unchecked")
  public void putScript(ScriptCache<Object> scriptCache) {
    descriptionMap.put(scriptCache.getDescription(), scriptCache);
    nameMap.put(scriptCache.getName(), scriptCache);
    classMap.put((Class<? extends Script<Object>>) scriptCache.getScript().getClass(), scriptCache);
  }
  public List<String> getScriptDescriptions() {
    return descriptionMap.values().stream()
        .filter(scriptCache -> !scriptCache.getScript().getInfo().isHidden())
        .map(ScriptCache::getDescription).filter(Strings::isNotBlank)
        .sorted().collect(Collectors.toList());
  }
  public void clear() {
    descriptionMap.clear();
    nameMap.clear();
    classMap.clear();
  }

}
