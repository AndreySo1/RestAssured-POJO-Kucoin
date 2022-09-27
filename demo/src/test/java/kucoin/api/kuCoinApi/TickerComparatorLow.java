package kucoin.api.kuCoinApi;

import java.util.Comparator;

import kucoin.api.kuCoinApi.entities.TickerData;

public class TickerComparatorLow implements Comparator<TickerData>{

   @Override
   public int compare(TickerData o1, TickerData o2) {
      float result = Float.compare(Float.parseFloat(o1.getChangeRate()), Float.parseFloat(o2.getChangeRate()));
      return (int)result;
   }
   
}
