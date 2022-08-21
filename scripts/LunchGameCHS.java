import com.duanxr.pgcon.core.detect.api.ImageCompare;
import com.duanxr.pgcon.core.detect.api.ImageCompare.Result;
import com.duanxr.pgcon.core.model.Area;
import com.duanxr.pgcon.script.api.ScriptInfo;
import com.duanxr.pgcon.script.engine.PGConScriptEngineV1;

/**
 * @author 段然 2022/7/25
 */
public class LunchGameCHS extends PGConScriptEngineV1<Object> {
  private static final ImageCompare.Param BACKUP = ImageCompare.Param.builder()
      .area(Area.ofRect(1236,796,162,54))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(false)
          .threshType(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template("{\"R\":54,\"C\":162,\"T\":0,\"D\":\"H/8BAP///////3kvAACIAHUfABkABh4AsQAPYQAuDnAAD6IAOB4AIgAPAQAID6IAKAlHAA9aAAocADYAD6EAGC4AAJMADvgAD6EABA9bABMDCgAPIgAHHwBEAQwPogBqDl0BDoIAD6IAJx3/XwAfAB4AAgQaAA+hAAwLgwAPDAIDBVgABR8ADzEDFw7UAAtoAQpPAA7oAg+iAAouAABKAAurAA/sARsPogBODUQCD4wCHw0gAAuTAQ5zAQ+iAB0v//+jACsOAAIOnAEPRAEYBR4ADEMBDr4EDqIACC0AATEGDqIAD8wDCA+iAC4OcgMPIAMUDSQAHwCiADYPHgYBHwCiAFIPEAULCIECD6IAAA+IAjQMQwICRQgP+QUTD6IABg6KAg8qAykPOAggGgCpBA+hBQIPRAExLwAAogAoCOcAD40AAw+IAh0OogAPYQURHgBJBR3/5gYPjgcGD6IAKR8AowkGH/8OAgwfACkDBAZWAxYAyQAP5gElDwYDEw4AAg+iAAwORwAPogAuD24EAg81AREa//YBD6IASR7/tgoPogAZCj8ADvYGD1QGJx4AiAgO1AMPKQ0EHwBKAwEOfgYPogAsDi8ID4EABQ+iAA0f/6IABQ86CDYKLAAOQwEPogAOHv99CQ5+AQ+IAh4IbAAOSQIPogAjAjkCD1MGLQ+YBwQOfwMP9gEWCSwDDogCD1QGLg7TAA6QAA+zABAEcAQPKgNFD6MABw9VARkfACoDAQ8xDyQPRAELDycLBA+qEA4PsgUCD9IPKQ6yBQhSBw7ICw/dBwgf/6IAQQ4KAAo6AQ7YAw9ICAgPogAeD7sGAwqKAgnDBA5+Ag+iABkPHQADD5IJAA+eCR8vAABmCgUHHQwPGBUbDOoAH/8BAP///////05Q//////8=\",\"L\":8748}")
      .build();
  private static final ImageCompare.Param SELECT_USER = ImageCompare.Param.builder()
      .area(Area.ofRect(84,500,232,68))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(false)
          .threshType(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template("{\"R\":68,\"C\":232,\"T\":0,\"D\":\"H/8BAP////////////////////94HwAiAA8fABUAAg/VAIoeABIACxQADyIAAw8VAAEPnABOD0sAKCgAAM8AABAAAxMAHwDBAAEBDAAP6AAFBjAACgEACD4ACxsADiQADzoBRAVNAA9+AAYIhwADzQAF1AAP6QAAD8UACC8AAOkAcA99AAcLCwAO0AEOMQEP6AA0D+kAOQeoAgPDAATNAAtvAg8NAQIPAgMDBxkADwwAEQ0ZAA7cAg/zADELQgAvAACcBAMPlAILCH0BC/MAD+gABQ+DAAIvAADpAEYPcQIDD4kAAA/aAgwM3wIM5wAP6ABtL///WAMMAR4AB40FA9kCDjsADLsEC2EDD+gAFgJUBhcAgwEv///oAFQMpgEPPAAFDUwAChUCDqADDugADs4AD6AHQg5RAA+NABMO1AAMKwEO0AEP6AAYH//oAFYOIgAP6AAPDTgAD3wDAw/oAFEOPwcPugIHB7AAD4gACg5BAA/oAGoHZAAO6AAPpwAVH/80BQwP6AARD6ADTBoADQYW/0gFA/MECQ8JD6IAEx4APQEOWAcODAAPuAJLDp4CDwwBCA/QARIOFQAO6AAPWAYPCrgGBWIMD3cEMx8ABAgKCjAAD+MKAA0zBw0rCA8oCGsPUQ0NLgD/hwMPswwBLwAA/ggFD+gAkwuWAA/oAAQP2QsKDtwAD7gCDgqIAA/oAD0LuQAP6AAWCBsBD5gNAA8oCHIP6AAbGwDoAA7FAw+ADgAPQAc0H/+4AlgHnwQOZggP0AQFD+gAJw/cAQcP6ABLBp4ECb8CDlAMDowAD+gAOA6uAw/oAB0DVxAI0gAI6AAJhQYKpwMP6AAKDygIEA72CA/fCA4PoANGCpEADi0RDugAD3AFHQ/nABMOvAIPTxAgDoULDwQIAA4lAA0oBA/oAE0OmQQPEAkQDrUGDwQJAw5UBA84FBsLdwAObAEPtwIEDxQBJQroAA8cBAwPfQwCD+gALx//6AAAD9ABSw7HAAs4AQ7nAA4YAg/oABEf/+gAbg4IAg4HFA/oABIOEQAPEhIMCyQBD1EEEg+IBCAPcAEHD7wTAAoWAA6gAwvoAA73AA8uAgMP6AAeDlkGD5gNGwtLAAl8AA/GAwkOeAAP6AAVHwDoAB8PKAglH/8dDQYO/gAP6AAGD54MCwIQAA8WAwoPcwMED0URPw5SGQ+dBRMGGQAPeAsMDvcADtMQDwEA//////////////////+2UP//////\",\"L\":15776}")
      .build();

  public LunchGameCHS() {
    super(ScriptInfo.builder()
        .isLoop(false)
        .isHidden(true)
        .description("LunchGameCHS")
        .build());
  }

  @Override
  public void execute() {
    press(A);
    sleep(1000);
    checkSelectUser();
    sleep(1000);
    checkBackUp();
  }

  private void checkSelectUser() {
    Result detect = detect(SELECT_USER);
    if (detect.getSimilarity() > 0.9) {
      press(A);
      sleep(1000);
    }
  }

  private void checkBackUp() {
    Result detect = detect(BACKUP);
    if (detect.getSimilarity() > 0.9) {
      press(D_RIGHT);
      sleep(150);
      press(A);
      sleep(1000);
    }
  }
}
