package com.Apple.Cars.Assignment;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.path.json.JsonPath;

public class RentalCarApiTest {
	
		@Test
		public void ApiCarTesting() throws JsonProcessingException, IOException {
			
			File f=new File("C:\\Users\\SatSang\\eclipse-workspace\\default\\Rental_car_Assignment\\src\\test\\resources\\ApiCar.json");
			JsonPath jpath= JsonPath.from(f);// retrieving the json responsefrom the file
			int NumCars=jpath.get("Car.size()");//no. of elements in the car node
			
			
			//Retrieving and displaying blue tesla cars 
			System.out.println("Blue Tesla Cars");
			for(int CarCount=0;CarCount<NumCars;CarCount++) {
				if(jpath.getString("Car["+CarCount+"].metadata.Color").equalsIgnoreCase("blue") &&
						jpath.getString("Car["+CarCount+"].make").equalsIgnoreCase("tesla")) {
					System.out.println(jpath.getString("Car["+CarCount+"]"));
					System.out.println("Notes: "+jpath.getString("Car["+CarCount+"].metadata.Notes"));
					System.out.println();
				}
			}
			
			
			//getting the minimum price 
			int lowPrice=jpath.get("Car.perdayrent.Price.min()");
			System.out.println("Details of Car with low rent: "+
					jpath.get("Car.findAll{it->it.perdayrent.Price == "+lowPrice+"}") );
			System.out.println();
			
			//Retrieving the cars which has lowest rate per day based on price and discount
			int price, discount;
			List<Float> ListCarsDiscount = new ArrayList<Float>();
			float lowPriceDiscount, CalPriceDiscount;
			//calculating discounts for all the cars
			for(int CarCount=0;CarCount<NumCars;CarCount++) {
				price=jpath.get("Car["+CarCount+"].perdayrent.Price");//price of the car per day
				discount=jpath.get("Car["+CarCount+"].perdayrent.Discount");//discount of the car per day
				CalPriceDiscount=price-(price*discount/100);//calculating the rate after discount
				ListCarsDiscount.add(CalPriceDiscount);// list of all the rates after discount
			}
			//Finding lowest pricing and discount in all the cars
			lowPriceDiscount=Collections.min(ListCarsDiscount);
			System.out.println("Cars which have the lowest per day rental cost based Price after discounts");
			//finding and displaying all the cars which has less price per day based on price and discount  
			for(int CarCount=0;CarCount<NumCars;CarCount++) {
				price=jpath.get("Car["+CarCount+"].perdayrent.Price");
				discount=jpath.get("Car["+CarCount+"].perdayrent.Discount");
				CalPriceDiscount=price-(price*discount/100);
				if(CalPriceDiscount==lowPriceDiscount)//checking for minimum discount 
					System.out.println(jpath.get("Car["+CarCount+"]")+"\n");// retrieving the cars based on the min rate after discount
					
			}
			
			//Finding the highest revenue generated car
			List<Float> listRevenueCars=new ArrayList<Float>();
			int  Days;
			//Calculating the revenues of all the cars
			for(int CarCount=0;CarCount<NumCars;CarCount++) {
				Days=jpath.get("Car["+CarCount+"].metrics.rentalcount.yeartodate");//no.of rental days
				listRevenueCars.add(ListCarsDiscount.get(CarCount)*Days);//adding revenues to list
			}
			//Finding maximum revenue in the list of revenues
			int RevenueCarIndex=listRevenueCars.indexOf(Collections.max(listRevenueCars));
			System.out.println("The highest revenue generating car\n"+jpath.get("Car["+RevenueCarIndex+"]"));
			System.out.println();
			
			//Calculating highest profit car
			List<Float> listProfit=new ArrayList<Float>();
			float yoyCost,depreciation;
			for(int CarCount=0;CarCount<NumCars;CarCount++) {
				yoyCost=jpath.get("Car["+CarCount+"].metrics.yoymaintenancecost");
				depreciation=jpath.get("Car["+CarCount+"].metrics.depreciation");
				listProfit.add(listRevenueCars.get(CarCount)-(yoyCost+depreciation));
			}
			int ProfitIndex=listProfit.indexOf(Collections.max(listProfit));
			System.out.println("The highest Profit car:\n"+jpath.get("Car["+ProfitIndex+"]")+"\n");
		}
	}
