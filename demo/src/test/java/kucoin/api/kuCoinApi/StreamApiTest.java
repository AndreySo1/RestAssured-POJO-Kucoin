package kucoin.api.kuCoinApi;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.http.ContentType;
import kucoin.api.kuCoinApi.entities.TickerData;
import kucoin.api.kuCoinApi.entities.TickerShort;

import static io.restassured.RestAssured.given;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StreamApiTest {

   public List<TickerData> getTickers(){
      return given()
      .contentType(ContentType.JSON)
      .when()
      .get("https://api.kucoin.com/api/v1/market/allTickers")
      .then()
      .extract().jsonPath().getList("data.ticker", TickerData.class);
   }
   
   @Test
   public void checkCrypto() {
      List<TickerData> usdTickers = getTickers().stream()
         .filter(x->x.getSymbol().endsWith("USDT")).collect(Collectors.toList());

      Assert.assertTrue(usdTickers.stream().allMatch(x->x.getSymbol().endsWith("USDT")), "not all tickets can to use USD");
   }

   @Test
   public void sortHightToLow() {
      List<TickerData> hightToLow = getTickers().stream()
         .filter(x->x.getSymbol().endsWith("USDT"))
         .sorted(new Comparator<TickerData>() {
            public int compare(TickerData o1, TickerData o2){
               return o2.getChangeRate().compareTo(o1.getChangeRate());
            }
         })
         .collect(Collectors.toList());

      List<TickerData> top10 = hightToLow.stream()
         .limit(10)
         .collect(Collectors.toList());
      
      Assert.assertTrue(Double.valueOf(top10.get(0).getChangeRate()) > Double.valueOf(top10.get(9).getChangeRate()));

   }

   @Test
   public void sortLowToHight() {
      List<TickerData> lowToHight = getTickers().stream()
         .filter(x->x.getSymbol().endsWith("USDT"))
         .sorted(new TickerComparatorLow())
         .limit(10)
         .collect(Collectors.toList());

      Assert.assertTrue(Double.valueOf(lowToHight.get(0).getChangeRate()) < Double.valueOf(lowToHight.get(9).getChangeRate()));
   }

   @Test
   public void map() {
      List<String> lowerCases = getTickers().stream()
         .map(x->x.getSymbol().toLowerCase())
         .collect(Collectors.toList()); //create new List with name coin

      Map<String, Float> usdMap = new HashMap<>(); 
      getTickers().stream()
         .forEach(x-> usdMap.put(x.getSymbol(), Float.parseFloat(x.getChangeRate())) ); // create new Map with name and cost coin

      Assert.assertTrue(usdMap.size() == lowerCases.size());
   }

   @Test
   public void shortClass() {
      List<TickerShort> shortList = new ArrayList<>();
      getTickers().forEach(x->shortList.add(new TickerShort(x.getSymbol(), Float.parseFloat(x.getChangeRate()))));
      //create List with short class (name, cost)

      Assert.assertNotEquals(shortList.get(0).getName(), shortList.get(1).getName());
   }
}
