
package com.app.stock.dto;

public class AnnualizedReturn implements Comparable<AnnualizedReturn> {


  private final String symbol;
  private final Double annualizedReturn;
  private final Double totalReturns;

  public AnnualizedReturn(String symbol, Double annualizedReturn, Double totalReturns) {
    this.symbol = symbol;
    this.annualizedReturn = annualizedReturn;
    this.totalReturns = totalReturns;
  }

  public String getSymbol() {
    return symbol;
  }

  public Double getAnnualizedReturn() {
    return annualizedReturn;
  }

  public Double getTotalReturns() {
    return totalReturns;
  }

  @Override
  public int compareTo(AnnualizedReturn otherReturn) {
    if(this.getAnnualizedReturn().compareTo(otherReturn.getAnnualizedReturn()) > 0) {
      return -1;
    } else if(this.getAnnualizedReturn().compareTo(otherReturn.getAnnualizedReturn()) < 0) {
      return 1;
    }
    return 0;
  }

}
