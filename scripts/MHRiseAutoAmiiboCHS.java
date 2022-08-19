import com.dooapp.fxform.annotation.FormFactory;
import com.duanxr.pgcon.core.detect.api.ImageCompare;
import com.duanxr.pgcon.core.detect.api.OCR;
import com.duanxr.pgcon.core.detect.api.OCR.ApiConfig;
import com.duanxr.pgcon.core.model.Area;
import com.duanxr.pgcon.gui.fxform.annotation.ConfigLabel;
import com.duanxr.pgcon.gui.fxform.factory.ReadOnlyLabelFactory;
import com.duanxr.pgcon.output.action.ButtonAction;
import com.duanxr.pgcon.script.api.ScriptInfo;
import com.duanxr.pgcon.script.engine.PGConScriptEngineV1;
import javafx.beans.property.SimpleIntegerProperty;
import lombok.Data;

/**
 * @author 2022/7/25
 */
public class MHRiseAutoAmiiboCHS extends PGConScriptEngineV1<MHRiseAutoAmiiboCHS.Config> {

  private static final ImageCompare.Param AMIIBO_READ = ImageCompare.Param.builder()
      .area(Area.ofRect(1296, 160, 122, 88))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .binaryThreshold(0.0)
          .inverse(false)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template(
          "{\"R\":88,\"C\":122,\"T\":0,\"D\":\"H/8BAP////9LEAABAA4VAA8BAC0PdQAQDSsADnYAD3wAPQ4rAA53AA98AEIPKwACLwAAewBPHwABAFIPegBnHwB5AAAPegBuD4sAAg+sAzUPegAJDwYBAi///3sANw8aAAYOgQEPegA9D/UAXg9KAAgfAHsASg5JAA97AFUORwAPewBTDkUAD5kADw97AAIPuQMeDr8AD3sAGQ6QAA96AAsPeQAED3ABLA95AD4PJQADD3sAAQ95AD4O9gAPcAEkD8MBAQ96ABQfAPUAGQ4UAA/zAB8OZgIPcAEQDigAD3kABg/rATIPeQA+D3sAFg8SAAcfAOUBAQ97AEAPeQA4D+ECFg9sATwPewAYD3kAQA/hAhUOeQAPygMFD/UAKg+jCAsPeQAKD3sASQ9eAgoPewA4Dy0AGw97AEkPXgI1H/+tBxQPXgI/D8oAER8AbAFHH/9mAhYfAGwBDw/rATsP5QFGH//MBA4PeQBKD/YACw9eAkMf/1ICEQ95AEkP9QAMLwAA1wIKD3sASA/XAgwPewBRD2wBAg97AFEPbAECD3sARQ95AA4PegBdDnkAD3sAPw95AFIPFAYDD14CCg97AFEPJgACD3sATQ95AFIPSQEDD+UBBA96AEMOWQAPeQBNL///egBnDlsAD3kABA97AEgPeQBeD7AVEC8A/3oAZS///3sAXQ7zAA97AEYPeQBeD9YLDQ97AF0OeQAPewBTLwAAegBtL///ewBWD3kAXgptAA55AA97AFIOXgIPewBWDl4CD3sATg95AGYv//95AGYf/3kAZi///3kAZg5QAw8BAP////9oUP//////\",\"L\":10736}")
      .build();
  private static final ImageCompare.Param AMIIBO_SUCCESS = ImageCompare.Param.builder()
      .area(Area.ofRect(930, 600, 64, 30))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .binaryThreshold(0.0)
          .inverse(false)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template(
          "{\"R\":30,\"C\":64,\"T\":0,\"D\":\"HwABAPgu//8LAAsMAA9AAAwd/woAAQwAEv8GAAYBAA9AAAYa/wkABhcAD0AAEQV3AAh/AAYXACX/AIoAD30ABB//ZgAAATMACUkAD0AAFh7/SgENEwAPgAAQH/9AABIOhQELtAAOQAEPQAAVDpoAD0AAPwUYAAftAQoAAQkiAA9+ARELLgAOQAAPQQAPCHQBL///AAIKCEgADlUDBQUDD0AAIwmMAA9AAAkOYAEKjAIPQAAMBWsDDlIAD0AAEQanAB//0QAAD0ABDQloAC7//9QDD8ABCQbfAQsNAA3/AQ9AAgIONwEOLgIOYAEO+wAODQEPQAAWDhMADhkADmgADwEAqFAAAAAAAA==\",\"L\":1920}")
      .build();
  private static final OCR.Param AMIIBO_USED = OCR.Param.builder()
      .area(Area.ofRect(102, 992, 92, 28))
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .binaryThreshold(0.0)
          .inverse(false)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .apiConfig(ApiConfig.builder()
          .method(OCR.Method.ENG)
          .build())
      .build();

  public MHRiseAutoAmiiboCHS() {
    super(ScriptInfo.<MHRiseAutoAmiiboCHS.Config>builder()
        .isLoop(true)
        .name("MHR Auto Amiibo(CHS.Ver)")
        .config(new Config())
        .build());
  }

  @Override
  public void execute() {
    info("{} times", addCount());
    script("PlusOneDayCHS");
    checkIfInAmiiboReading();
    checkIfAmiiboSuccess();
    lottery();
    checkIfInAmiiboReading();
  }

  private Integer addCount() {
    int count = config.getAmiiboCount().get();
    config.getAmiiboCount().set(count + 1);
    return count;
  }

  private void checkIfInAmiiboReading() {
    until(() -> detect(AMIIBO_READ),
        result -> result.getSimilarity() > 0.9,
        () -> {
          press(ButtonAction.A);
          sleep(150);
          checkIfAmiiboUsed();
        });
  }

  private void checkIfAmiiboSuccess() {
    until(() -> detect(AMIIBO_SUCCESS),
        result -> result.getSimilarity() > 0.9,
        () -> sleep(150));
  }

  private void lottery() {
    for (int i = 0; i < 20; i++) {
      press(ButtonAction.A);
      sleep(100);
    }
  }

  private void checkIfAmiiboUsed() {
    if (containAmiibo(detect(AMIIBO_USED))) {
      press(ButtonAction.B);
      sleep(150);
      press(ButtonAction.D_LEFT);
      sleep(150);
      press(ButtonAction.A);
      sleep(150);
      press(ButtonAction.A);
      sleep(150);
    }
  }

  private boolean containAmiibo(OCR.Result result) {
    return result.getTextWithoutSpace().toLowerCase().contains("amiibo");
  }

  @Data
  public static class Config {

    @ConfigLabel("Amiibo Count")
    @FormFactory(ReadOnlyLabelFactory.class)
    private SimpleIntegerProperty amiiboCount = new SimpleIntegerProperty(0);

  }
}
