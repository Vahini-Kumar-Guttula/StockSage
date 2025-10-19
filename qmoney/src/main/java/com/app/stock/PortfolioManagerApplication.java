
package com.app.stock;


import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import com.app.stock.dto.AnnualizedReturn;
import com.app.stock.dto.Candle;
import com.app.stock.dto.PortfolioTrade;
import com.app.stock.dto.TiingoCandle;
import com.app.stock.dto.TotalReturnsDto;
import com.app.stock.log.UncaughtExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.ThreadContext;
import com.app.stock.portfolio.PortfolioManager;
import com.app.stock.portfolio.PortfolioManagerFactory;
import org.springframework.web.client.RestTemplate;


public class PortfolioManagerApplication {

  public static RestTemplate restTemplate = new RestTemplate();
  public static PortfolioManager portfolioManager = PortfolioManagerFactory.getPortfolioManager(restTemplate);

  private static String token = "6c1b98d3a91e74556cba93c5a1b8fab5de6f6899";

  public static String getToken() {
    return token;
  }

  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {
    List<PortfolioTrade> trades = readTradesFromJson(args[0]);
    List<String> symbols = new ArrayList<>();
    for(PortfolioTrade trade : trades) {
      symbols.add(trade.getSymbol());
    }
    return symbols;
  }


  private static void printJsonObject(Object object) throws IOException {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }

  private static File resolveFileFromResources(String filename) throws URISyntaxException {
    return Paths.get(
        Thread.currentThread().getContextClassLoader().getResource(filename).toURI()).toFile();
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }


  public static List<String> debugOutputs() {

     String valueOfArgument0 = "assessments/trades.json";
     String resultOfResolveFilePathArgs0 = "/home/crio-user/workspace/vahinikumarg-ME_QMONEY_V2/qmoney/bin/test/assessments/trades.json";
     String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@1573f9fc";
     String functionNameFromTestFileInStackTrace = "mainReadFile";
     String lineNumberFromTestFileInStackTrace = "19";


    return Arrays.asList(new String[]{valueOfArgument0, resultOfResolveFilePathArgs0,
        toStringOfObjectMapper, functionNameFromTestFileInStackTrace,
        lineNumberFromTestFileInStackTrace});
  }


  // Note:
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.
  public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException {
      List<PortfolioTrade> trades = readTradesFromJson(args[0]);
      List<TotalReturnsDto> totalReturnsDtos = new ArrayList<>();
      LocalDate endDate = LocalDate.parse(args[1]);
      for(PortfolioTrade trade : trades) {
        List<Candle> candles = fetchCandles(trade, endDate, token);
        Double closingPrice = getClosingPriceOnEndDate(candles);
        totalReturnsDtos.add(new TotalReturnsDto(trade.getSymbol(), closingPrice));
      }
      Collections.sort(totalReturnsDtos);

      List<String> symbols = new ArrayList<>();
      for(TotalReturnsDto dto : totalReturnsDtos) {
        symbols.add(dto.getSymbol());
      }
      return symbols;
  }


  public static List<PortfolioTrade> readTradesFromJson(String filename) throws IOException, URISyntaxException {
    File inputFile = resolveFileFromResources(filename);
    PortfolioTrade[] trades = getObjectMapper().readValue(inputFile, PortfolioTrade[].class);
    return List.of(trades);
  }


  public static String prepareUrl(PortfolioTrade trade, LocalDate endDate, String token) {
    String url = "https://api.tiingo.com/tiingo/daily/" + trade.getSymbol() + "/prices?startDate=" + trade.getPurchaseDate() + "&endDate=" + endDate + "&token=" + token;
    return url;
  }




  static Double getOpeningPriceOnStartDate(List<Candle> candles) {
     return candles.get(0).getOpen();
  }


  public static Double getClosingPriceOnEndDate(List<Candle> candles) {
     return candles.get(candles.size()-1).getClose();
  }


  public static List<Candle> fetchCandles(PortfolioTrade trade, LocalDate endDate, String token) {
    RestTemplate restTemplate = new RestTemplate();
    String url = prepareUrl(trade, endDate, token);
    Candle[] candles = restTemplate.getForObject(url, TiingoCandle[].class);
    return List.of(candles);
  }

  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args)
      throws IOException, URISyntaxException {
        List<PortfolioTrade> trades = readTradesFromJson(args[0]);
        LocalDate endDate = LocalDate.parse(args[1]);
        List<AnnualizedReturn> returns = new ArrayList<>();

        for(PortfolioTrade trade : trades) {
          List<Candle> candles = fetchCandles(trade, endDate, token);
          AnnualizedReturn annualizedReturn = calculateAnnualizedReturns(endDate, trade, getOpeningPriceOnStartDate(candles), getClosingPriceOnEndDate(candles));
          returns.add(annualizedReturn);
        }
        Collections.sort(returns);
     return returns;
  }



  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
      PortfolioTrade trade, Double buyPrice, Double sellPrice) {
        Double totalReturns = (sellPrice * trade.getQuantity() - buyPrice * trade.getQuantity())/ (buyPrice * trade.getQuantity());
        Long noOfDays = ChronoUnit.DAYS.between(trade.getPurchaseDate(), endDate);
        Double noOfYears = (double) (noOfDays)/365 ;
        Double numerator = (double) (1) / noOfYears;
        Double annualizedReturns = Math.pow(1 + totalReturns, numerator) -1;
      return new AnnualizedReturn(trade.getSymbol(), annualizedReturns, totalReturns);
  }

  public static List<AnnualizedReturn> mainCalculateReturnsAfterRefactor(String[] args)
      throws Exception {
       String file = args[0];
       LocalDate endDate = LocalDate.parse(args[1]);
       List<PortfolioTrade> trades = readTradesFromJson(file);
       //String contents = readFileAsString(file);
       //ObjectMapper objectMapper = getObjectMapper();
       //PortfolioTrade[] portfolioTrades = objectMapper.readValue(trades, PortfolioTrade[].class);
       return portfolioManager.calculateAnnualizedReturn(trades, endDate);
  }


  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());

    printJsonObject(mainCalculateReturnsAfterRefactor(args));
  }
}

