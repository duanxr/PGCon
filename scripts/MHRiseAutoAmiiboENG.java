import com.dooapp.fxform.annotation.FormFactory;
import com.duanxr.pgcon.core.detect.api.ImageCompare;
import com.duanxr.pgcon.core.detect.api.OCR;
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
public class MHRiseAutoAmiiboENG extends PGConScriptEngineV1<MHRiseAutoAmiiboENG.Config> {

  private static final ImageCompare.Param AMIIBO_READ = ImageCompare.Param.builder()
      .area(Area.ofRect(1296,160,118,82))
      .method(ImageCompare.Method.TM_CCORR)
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(false)
          .threshType(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template("{\"R\":82,\"C\":118,\"T\":0,\"D\":\"H/8BAP////88HwABACYPXAAcD3IABA+AAD0OJwAOcwAPeAA+DycAAi8AAHcASx8AAQBSD3YAci8AAHcAYQ0WAA7+Ag97AzIPFgACD/4AAi///3cANw8WAAIOdQEPdgA9D+0AWg9KAAQfAHcASg5JAA6OAA53AA/rADcP7gBXDkUAD+4AIA+ZAx4OuwAPdwAVDowAD3YAFg4wAQ9kASIPdQAlD3cATQ9CAQIPdwAOD2QBDQ91ADov/wDbARQPZAACD2ABEg9SAioP2QABD3UADA/uADoPdQAUD3cAMw8SAAcfANUBAQ93ADwPdQA0D8kCFg9gATgPdwAYD3UAPA/JAhUOdQAPqgMFD+0AJg9bCAsPdQAKD3cARQ9KAgoPdwA0Dy0AGw93AEUPSgIxH/9tBxAPdQBGDwcFCw9gAUMf/1ICDg9pBhgP2wE3D2ABQx//pAQND3UARg/uAAsPlAQQD3cAQQ91AEYPUgILLwAAvwIKD3cATg91AAIPdwBND2ABAg93AE0PYAECD3cAQQ91AA4PdgBZDnUAD3cAOw/VAQwPdwBFD3UATg9jAAMP1QEID3cASQ91AE4PdwADD0oCBQ93AEwP6gACD3YAsA7TEA91AAQPdwBED3UAWg5bAA91AAAPdgBhL///dwBZDusAD3cAQg91AFoPcgsND3cAWQ51AA93AE8vAAB2AGkv//93AFIPdQBaD/0LAy///3cAVg91AFoPvhwBD3cAUg91AGIv//91AGIf/3UAYg4eBA92AFwf/wEA/4BQ//////8=\",\"L\":9676}")
      .build();
  private static final ImageCompare.Param AMIIBO_SUCCESS = ImageCompare.Param.builder()
      .area(Area.ofRect(818,534,276,40))
      .method(ImageCompare.Method.TM_CCORR)
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(true)
          .threshType(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template("{\"R\":40,\"C\":276,\"T\":0,\"D\":\"H/8BAP////////+kIQAABQACBgAPwACiAr0AD9QAAQ9TACcEBgAOXQAPEwGVHgAUAA8UASke/xQBDycCsA4oAg9fACIPFAGnDskADygCDg8bAAQfABQBqh4AtAAPFAEWJgAAcQMWAA0ABhYAC1AEBy8ABj4ADpcBB1MAC2UADjQAChQABRsADi8ADHEACA0AD2MABAQKAAg0AAkUAQhaAApkAAlBAB0AFQAOFAEdAD0ACpcBCRQBD2UABC///4UABArMAQ9xAAYOVgALDAAZABQBHwAUAQEJRwENFAEFQQAJBwAPKAIFDEQBDygCBAzZAANlAC7//xQBH/8UAQwfAMIBAgemAAVDAAZiAArUAA8oAgQKxQEL1wEvAADRAAUIFAEGIQAOTwALywAHWAAu//8NAA9IAQMNFAEGDgAPSQAFBigADOcACb4DDxQBDQaPAA+BAAAPQAANBpYAL///FQEBCxQBDWUAHwDzBAIDGQUPBgECAkQAB2sEDPYCBikACMADH/8UAQ8MIAAMZgUPFAElH/8UAQkMHQUOYwUJ6AUf/+sABgspBgrJAwtCAAZPAA8UARcOWgAPFAEJH/8UARwPeAQKD4QEAAbgAA8UASYPcQUBDygCFh8AFAFUDhAHD44HFA8UASEPbgICHwBQBBIv//97AwUPFAErD/UBAQdpDA6MBQ+FDAYL1gAPUAQHCo0ACUMADocADxQBAwiXAAiiAQ35Bg8UARcMaAAPFAEKDkkBBxAICYYADxQBBA2FAR//dgIDDmMAD5gBAQ6SAg5jCQ88AxgPjAcNCU4ABS4AD1AHBC///3AABQhFAAsXCA5/Bwo8AQ6MBw8UAREPqQQED3gGEgu/BA4wBQxpAQknAAkBAA/VBAQPDgAFD3YLCC///zQACg5kAA8UAQkKggAPKAISDtcEDi8FCKUEDmUAD3gGCw4HAQ9IBQUPLgEPDpkDDhQBD+kCBx//FAEGF/8BCQb5CwnjASz//y4FC/0EBmUADwQODwc3Aw7uAw8OAAEPQwAAD/AIBQVXACoAAKgALwD/AQD8DtcBDwEA6Q8nAv8FDskDDxQB8A86A/8GDwEA////////////sVD//////w==\",\"L\":11040}")
      .build();
  private static final ImageCompare.Param AMIIBO_USED = ImageCompare.Param.builder()
      .area(Area.ofRect(112,984,370,42))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(true)
          .threshType(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template("{\"R\":42,\"C\":370,\"T\":0,\"D\":\"H/8BAGZfAAAAAP8BAP//////////////EA8aC3IvAADVAFEfAPsAQh8ATAAcD40MPS8AAHIBch8AFgADD3UAOgI7AQgGAA83ABUP6wA6D3IBgS4AAJwADvoOD3IBLQ9xDxgPwQ9wDmoDD/kBOASUAAxlAA51AA/kAlIPcgHqH//CBTMPcgGjDoUAD3IBTg9xAEYPIwEPAgEACxIACgkBBgsFByoAC0MAB94ACGkDCTQALwAADwAAAzYALwAAgQEBLgAAgwANrgALDgANcQEDYAADawAfAFEAAR8A1QACAw0AB2gAChcAB+QCC0cBCkIAD3EBAS0AAIIBCJ0AA7MEC1YADbQBBjkABxEAD14AAQ5+AAluAB4APQILhAAPLwEBLwAAQQIGBG0BDh0CD9UACwxXAAoXAAjIBQ5IAQ9zARIrAABzAQfuAAccAA9BAAYKRgAPXgALL/8AbgAJD3IBAw5BAA4aAQ5sAg6NAA/VABcPcQECLgAAcgEP2wAJDXIBCvIBBgECCLEBH/9XAwAr//8kAAzkAg9QAgAGQgAKKgEPcgEWH/+uAAgH7gAPyAUCCdEFBXIBB0oADtUACyQBCiwBCnIBDDoDDi0BBHIBCl0ADUABCpwACxIBD3IBBwxeAB8AXgAFD24AAQ9yAQwHoAIKEwIOlwQGQAELjQAf/3UABx7/TgUPLAEGDLIBD4sACx7/WQIHhgQPDgIAAAcAC7gBD3IBBQeeAwfgBR//1AIBCjUCD3IBDQujBQYlAwYwBw/kAgcLSQEPhQICClUHDYcCD3IBGx//cgEKDnYHDqsED/UECg9yAQAPawUBD3IBCw7gAwxyAQ+4BQIJoggP5AILCN4CHwAPCAkPcgEaCG4AHgD+AQ/kAgcNAQYPcgENHwByAQINHAAvAACYBwYNTAMPcgEHCawID3IBFA45BAW9AQljAg9yARofAHIBNR4A8wYNRwkPcQEGC4YAD3IBBQ9WBAAPcgEXD+8JAg5WBA9yARgMYwoN1AAPcgEbHwByARsLVAACpgANlQMIogwPhgAIDt4DDiwADXIBD20CAA+4AgYIkgAHcw0POgcIDC8DHf8TBg3VAA9yARYfAHIBJQ8dAQAPBwUAHgB7CQ9WBAQObAMP7gUDC24AD2gGBA66CA4hCA/kAhoOHQIO+QMPcgEfDkkBD+QCDQ4EAQ70Bw9yAQUMVgQPXgAAD3IBGA4MCQ60AQ7CAQ/kAhcNTgEOdAAPcgEVDtwAD3IBDg0aBQRdCA6VBQ/zBAIfADAEAA/qBggOGgYNLAELYQAPDgALH/9NCQsPcgEBD9UAEh8AcgEVDosAD9AABAsOAw4FAQ4/AS///54ACgt2BA4TAQ5yAQ+FAwYOPgkPbQoEDugAD3IBCw5RAA9yASUPuQEBD3EBBQgFBy8AALUAAw78AQ+7CgIJaQUONAAOJwMOmAAPMAwBCWAADlEADg4AD+wJBgkFBgelAA/VAAEPQQIECkEAD+QCBA75AA8BAEAPVwA3DqMAD/0eUQ8YAY8P+wBIDv0ADwEAqA9xAf//AQ74BA9yAf9OD3EB/18OwQYPAQD/////////////4FD//////w==\",\"L\":15540}")
      .build();

  public MHRiseAutoAmiiboENG() {
    super(ScriptInfo.<MHRiseAutoAmiiboENG.Config>builder()
        .isLoop(true)
        .description("MHR Auto Amiibo(ENG.Ver)")
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
    if (detect(AMIIBO_USED).getSimilarity()>0.9) {
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
