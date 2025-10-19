
package com.app.stock.portfolio;

import com.app.stock.dto.AnnualizedReturn;
import com.app.stock.dto.PortfolioTrade;
import com.app.stock.exception.StockQuoteServiceException;
import java.time.LocalDate;
import java.util.List;

public interface PortfolioManager {


  //CHECKSTYLE:OFF


  List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades,
      LocalDate endDate)
      throws StockQuoteServiceException
  ;
}

