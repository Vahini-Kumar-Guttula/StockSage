package com.utils;

import java.util.Comparator;
import com.app.stock.dto.Candle;

public class CandleComparator implements Comparator<Candle> {

    @Override
    public int compare(Candle candle1, Candle candle2) {
        if(candle1.getDate().isAfter(candle2.getDate())) {
            return 1;
        } else if(candle1.getDate().isBefore(candle2.getDate())) {
            return -1;
        }
        return 0;
    }
    
}
