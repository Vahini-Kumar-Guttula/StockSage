
package com.app.stock.portfolio;

import com.app.stock.dto.AnnualizedReturn;
import com.app.stock.dto.Candle;
import com.app.stock.dto.PortfolioTrade;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.app.stock.quotes.StockQuotesService;
import com.app.stock.exception.StockQuoteServiceException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {

  private StockQuotesService stockQuotesService;
  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  @Deprecated
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
  }

  protected PortfolioManagerImpl(StockQuotesService stockQuotesService) {
    this.stockQuotesService = stockQuotesService;
  }



  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws StockQuoteServiceException, JsonProcessingException {
        if(from.compareTo(to) >= 0) {
           throw new RuntimeException();         
        }
      // String url = buildUri(symbol, from, to);
      // TiingoCandle[] stocksStartToEndDate = restTemplate.getForObject(url, TiingoCandle[].class);
      // List<Candle> stocksList = Arrays.asList(stocksStartToEndDate); 
      // return stocksList;
      return stockQuotesService.getStockQuote(symbol, from, to);
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {

      String token = "6c1b98d3a91e74556cba93c5a1b8fab5de6f6899";
      String uriTemplate = "https://api.tiingo.com/tiingo/daily/$SYMBOL/prices?" + "startDate=$STARTDATE&endDate=$ENDDATE&token=$APIKEY";
      String url = uriTemplate.replace("$APIKEY", token).replace("$SYMBOL", symbol) .replace("$STARTDATE", startDate.toString()) 
      .replace("$ENDDATE", endDate.toString());
      return url;
  }


  public AnnualizedReturn getAnnualizedReturn (PortfolioTrade trade, LocalDate endLocalDate) throws StockQuoteServiceException {
    AnnualizedReturn annualizedReturn;
    String symbol = trade.getSymbol();
    LocalDate startLocalDate = trade.getPurchaseDate();
  
    try {
      List<Candle> stocksStartToEndDate;
      stocksStartToEndDate = getStockQuote(symbol, startLocalDate, endLocalDate);
      Candle stockLatest = stocksStartToEndDate.get(stocksStartToEndDate.size()-1);
      Candle stockStartDate = stocksStartToEndDate.get(0);
      
      Double buyPrice = stockStartDate.getOpen();
      Double sellPrice = stockLatest.getClose();
  
      Double totalReturn = (sellPrice - buyPrice) / buyPrice;
  
      Double numYears = (double) ChronoUnit.DAYS.between (startLocalDate, endLocalDate) / 365;
      Double annualizedReturns = Math.pow((1+ totalReturn), (1/numYears)) - 1;
  
      annualizedReturn = new AnnualizedReturn (symbol, annualizedReturns, totalReturn);
    } catch (JsonProcessingException e) {
      annualizedReturn = new AnnualizedReturn (symbol, Double.NaN, Double.NaN);
    }
    return annualizedReturn;
  }

  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades,
      LocalDate endDate) throws StockQuoteServiceException {
    // TODO Auto-generated method stub
     AnnualizedReturn annualizedReturn;
    List<AnnualizedReturn> annualizedReturns = new ArrayList<AnnualizedReturn>();

    // for(PortfolioTrade trade : portfolioTrades) {
    //   List<Candle> candles;
    //   try {
    //     candles = stockQuotesService.getStockQuote(trade.getSymbol(), trade.getPurchaseDate(), endDate);
    //     annualizedReturn = getAnnualizedReturn (trade, endDate);
    //     annualizedReturns.add(annualizedReturn);
    //   } catch (Exception e) {
    //     e.printStackTrace();
    //   } 
    // }

    for (int i = 0; i < portfolioTrades.size(); i++) {
        annualizedReturn = getAnnualizedReturn (portfolioTrades.get(i), endDate);
        annualizedReturns.add(annualizedReturn);
    }
    Comparator<AnnualizedReturn> SortByAnReturn = Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
    Collections.sort(annualizedReturns, SortByAnReturn);
    return annualizedReturns;
  }






  //  Modify the function #getStockQuote and start delegating to calls to
  //  stockQuoteService provided via newly added constructor of the class.
  //  You also have a liberty to completely get rid of that function itself, however, make sure
  //  that you do not delete the #getStockQuote function.

}
