
package com.app.stock.quotes;

import com.app.stock.dto.AlphavantageCandle;
import com.app.stock.dto.AlphavantageDailyResponse;
import com.app.stock.dto.Candle;
import com.app.stock.exception.StockQuoteServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.utils.CandleComparator;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlphavantageService implements StockQuotesService {

  // url = https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=IBM&outputsize=full&apikey=4AK09MFIXOXKS3EN

  private RestTemplate restTemplate;

  protected AlphavantageService(RestTemplate restTemplate) {
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
     AlphavantageDailyResponse dailyResponse = getObjectMapper().readValue(candleString, AlphavantageDailyResponse.class);

     List<Candle> candles = new ArrayList<>();

     dailyResponse.getCandles().keySet().forEach(currentDate -> {
      AlphavantageCandle candle = dailyResponse.getCandles().get(currentDate);
      candle.setDate(currentDate);

      if(currentDate.isAfter(to) || currentDate.isBefore(from)) {
        return;
      }
      candles.add(candle);
     });
     Collections.sort(candles, new CandleComparator());
     return candles; 
    } catch(NullPointerException e) {
      throw new StockQuoteServiceException("Alphavantage returned invalid response", e);
    }
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    String token = "4AK09MFIXOXKS3EN";
    String url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=" + symbol + "&outputsize=full&apikey=" + token;
    return url;
}

private static ObjectMapper getObjectMapper() {
  ObjectMapper objectMapper = new ObjectMapper();
  objectMapper.registerModule(new JavaTimeModule());
  return objectMapper;
}

}

