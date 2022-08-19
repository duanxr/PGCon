package com.duanxr.pgcon.component;

import com.duanxr.pgcon.exception.AlertException;
import com.duanxr.pgcon.output.api.Protocol;
import com.duanxr.pgcon.output.impl.easycon.EasyConProtocolV140;
import com.duanxr.pgcon.output.impl.easycon.EasyConProtocolV147;
import com.duanxr.pgcon.output.impl.pgcon.PGConProtocol;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/7/28
 */
@Component
public class ProtocolManager {

  private final LinkedHashMap<String, Function<String, Protocol>> supportedProtocols;

  public ProtocolManager() {
    supportedProtocols = new LinkedHashMap<>();
    supportedProtocols.put("EasyCon V1.47", EasyConProtocolV147::new);
    supportedProtocols.put("EasyCon V1.40", EasyConProtocolV140::new);
    supportedProtocols.put("PGCon", PGConProtocol::new);
  }

  public List<String> getProtocolList() {
    return supportedProtocols.keySet().stream().toList();
  }

  public Protocol loadProtocol(String protocolName, String port) {
    Function<String, Protocol> protocolLoader = supportedProtocols.get(protocolName);
    if (protocolLoader == null) {
      throw new AlertException("不支持的固件协议:" + protocolName);
    }
    return protocolLoader.apply(port);
  }
}
