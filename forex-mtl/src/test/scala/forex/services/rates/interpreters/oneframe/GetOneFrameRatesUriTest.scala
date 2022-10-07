package forex.services.rates.interpreters.oneframe

class GetOneFrameRatesUriTest extends org.scalatest.funspec.AnyFunSpec {

  describe("GetOneFrameRatesUri") {
    describe("should construct a OneFrame url for all possible combinations of currencies") {
      it("Should construct a uri with a port") {
        val result = GetOneFrameRatesUri("oneframe.com", Some(8080))

        assert(result.renderString === "oneframe.com:8080/rates?pair=AUDAUD&pair=AUDCAD&pair=AUDCHF&pair=AUDEUR&pair=AUDGBP&pair=AUDNZD&pair=AUDJPY&pair=AUDSGD&pair=AUDUSD&pair=CADAUD&pair=CADCAD&pair=CADCHF&pair=CADEUR&pair=CADGBP&pair=CADNZD&pair=CADJPY&pair=CADSGD&pair=CADUSD&pair=CHFAUD&pair=CHFCAD&pair=CHFCHF&pair=CHFEUR&pair=CHFGBP&pair=CHFNZD&pair=CHFJPY&pair=CHFSGD&pair=CHFUSD&pair=EURAUD&pair=EURCAD&pair=EURCHF&pair=EUREUR&pair=EURGBP&pair=EURNZD&pair=EURJPY&pair=EURSGD&pair=EURUSD&pair=GBPAUD&pair=GBPCAD&pair=GBPCHF&pair=GBPEUR&pair=GBPGBP&pair=GBPNZD&pair=GBPJPY&pair=GBPSGD&pair=GBPUSD&pair=NZDAUD&pair=NZDCAD&pair=NZDCHF&pair=NZDEUR&pair=NZDGBP&pair=NZDNZD&pair=NZDJPY&pair=NZDSGD&pair=NZDUSD&pair=JPYAUD&pair=JPYCAD&pair=JPYCHF&pair=JPYEUR&pair=JPYGBP&pair=JPYNZD&pair=JPYJPY&pair=JPYSGD&pair=JPYUSD&pair=SGDAUD&pair=SGDCAD&pair=SGDCHF&pair=SGDEUR&pair=SGDGBP&pair=SGDNZD&pair=SGDJPY&pair=SGDSGD&pair=SGDUSD&pair=USDAUD&pair=USDCAD&pair=USDCHF&pair=USDEUR&pair=USDGBP&pair=USDNZD&pair=USDJPY&pair=USDSGD&pair=USDUSD")
      }

      it("Should construct a uri without a port") {
        val result = GetOneFrameRatesUri("oneframe.com", None)

        assert(result.renderString === "oneframe.com/rates?pair=AUDAUD&pair=AUDCAD&pair=AUDCHF&pair=AUDEUR&pair=AUDGBP&pair=AUDNZD&pair=AUDJPY&pair=AUDSGD&pair=AUDUSD&pair=CADAUD&pair=CADCAD&pair=CADCHF&pair=CADEUR&pair=CADGBP&pair=CADNZD&pair=CADJPY&pair=CADSGD&pair=CADUSD&pair=CHFAUD&pair=CHFCAD&pair=CHFCHF&pair=CHFEUR&pair=CHFGBP&pair=CHFNZD&pair=CHFJPY&pair=CHFSGD&pair=CHFUSD&pair=EURAUD&pair=EURCAD&pair=EURCHF&pair=EUREUR&pair=EURGBP&pair=EURNZD&pair=EURJPY&pair=EURSGD&pair=EURUSD&pair=GBPAUD&pair=GBPCAD&pair=GBPCHF&pair=GBPEUR&pair=GBPGBP&pair=GBPNZD&pair=GBPJPY&pair=GBPSGD&pair=GBPUSD&pair=NZDAUD&pair=NZDCAD&pair=NZDCHF&pair=NZDEUR&pair=NZDGBP&pair=NZDNZD&pair=NZDJPY&pair=NZDSGD&pair=NZDUSD&pair=JPYAUD&pair=JPYCAD&pair=JPYCHF&pair=JPYEUR&pair=JPYGBP&pair=JPYNZD&pair=JPYJPY&pair=JPYSGD&pair=JPYUSD&pair=SGDAUD&pair=SGDCAD&pair=SGDCHF&pair=SGDEUR&pair=SGDGBP&pair=SGDNZD&pair=SGDJPY&pair=SGDSGD&pair=SGDUSD&pair=USDAUD&pair=USDCAD&pair=USDCHF&pair=USDEUR&pair=USDGBP&pair=USDNZD&pair=USDJPY&pair=USDSGD&pair=USDUSD")
      }
    }
  }
}
