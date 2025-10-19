
package com.app.stock.quotes;

import com.app.stock.dto.Candle;
import com.app.stock.dto.TiingoCandle;
import com.app.stock.exception.StockQuoteServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.List;
import org.springframework.web.client.RestTemplate;

public class TiingoService implements StockQuotesService {

  private RestTemplate restTemplate;

  protected TiingoService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException, StockQuoteServiceException {
      if(from.compareTo(to) >= 0) {
          throw new RuntimeException();         
       }
    try {
     String url = buildUri(symbol, from, to);
     String candleString = restTemplate.getForObject(url, String.class);
     Candle[] candles = getObjectMapper().readValue(candleString, TiingoCandle[].class);
     return List.of(candles); 
    } catch(NullPointerException e) {
      throw new StockQuoteServiceException("Error occured when requesting response from Tiingo API", e.getCause());
    }
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    String token = "6c1b98d3a91e74556cba93c5a1b8fab5de6f6899";
    String uriTemplate = "https://api.tiingo.com/tiingo/daily/$SYMBOL/prices?" + "startDate=$STARTDATE&endDate=$ENDDATE&token=$APIKEY";
    String url = uriTemplate.replace("$APIKEY", token).replace("$SYMBOL", symbol) .replace("$STARTDATE", startDate.toString()) 
    .replace("$ENDDATE", endDate.toString());
    return url;
}

private static ObjectMapper getObjectMapper() {
  ObjectMapper objectMapper = new ObjectMapper();
  objectMapper.registerModule(new JavaTimeModule());
  return objectMapper;
}

}
