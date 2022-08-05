package com.duanxr.pgcon.component;

import com.duanxr.pgcon.gui.exception.GuiAlertException;
import com.duanxr.pgcon.output.api.Protocol;
import com.duanxr.pgcon.output.impl.ec.EasyConProtocol;
import com.duanxr.pgcon.output.impl.pc.PGConProtocol;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.BiFunction;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/7/28
 */
@Component
public class ProtocolManager {

  private final LinkedHashMap<String, BiFunction<String, Integer, Protocol>> supportedProtocols;

  public ProtocolManager() {
    supportedProtocols = new LinkedHashMap<>();
    supportedProtocols.put("EasyCon V1.4", EasyConProtocol::new);
    supportedProtocols.put("PGCon", PGConProtocol::new);
  }

  public List<String> getProtocolList() {
    return supportedProtocols.keySet().stream().toList();
  }

  public Protocol loadProtocol(String protocolName, String port, Integer baudRate) {
    BiFunction<String, Integer, Protocol> protocolLoader = supportedProtocols.get(protocolName);
    if (protocolLoader == null) {
      throw new GuiAlertException("不支持的固件协议:" + protocolName);
    }
    return protocolLoader.apply(port, baudRate);
  }
}
