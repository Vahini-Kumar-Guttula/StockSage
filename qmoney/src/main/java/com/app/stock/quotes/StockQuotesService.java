package com.app.stock.quotes;

import com.app.stock.dto.Candle;
import com.app.stock.exception.StockQuoteServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.time.LocalDate;
import java.util.List;

public interface StockQuotesService {



  //  Change the method signature to throw StockQuoteServiceException

  //CHECKSTYLE:OFF
  List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException
      , StockQuoteServiceException
  ;
  //CHECKSTYLE:ON

}
