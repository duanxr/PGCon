package com.duanxr.pgcon.script.component;

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
  
  public ScriptManager() {
    this.descriptionMap = new ConcurrentHashMap<>();
    this.nameMap = new ConcurrentHashMap<>();
  }

  public ScriptCache<Object> getScriptByDescription(String scriptName) {
    return descriptionMap.get(scriptName);
  }

  public ScriptCache<Object> getScriptByName(String scriptName) {
    return nameMap.get(scriptName);
  }

  public Collection<ScriptCache<Object>> getScripts() {
    return descriptionMap.values();
  }


  public void putScript(ScriptCache<Object> scriptCache) {
    descriptionMap.put(scriptCache.getDescription(), scriptCache);
    nameMap.put(scriptCache.getName(), scriptCache);
  }

  public List<String> getScriptDescriptions() {
    return descriptionMap.values().stream()
        .filter(scriptCache -> !scriptCache.getScript().getInfo().isHidden())
        .map(ScriptCache::getDescription).filter(Strings::isNotBlank)
        .sorted().collect(Collectors.toList());
  }
}
