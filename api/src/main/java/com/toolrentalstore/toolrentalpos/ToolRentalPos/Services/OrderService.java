package com.toolrentalstore.toolrentalpos.ToolRentalPos.Services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.toolrentalstore.toolrentalpos.ToolRentalPos.Models.Cart;
import com.toolrentalstore.toolrentalpos.ToolRentalPos.Models.CartItem;
import com.toolrentalstore.toolrentalpos.ToolRentalPos.Models.Product;
import com.toolrentalstore.toolrentalpos.ToolRentalPos.Repositories.ProductRepository;

@Service
public class OrderService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    public OrderService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Map<String,Integer>> getHolidaysForYear(int year) {
        List<Map<String,Integer>> holidays = new ArrayList<>();
        // July 4th, take the 4th 100% & if it's on a weekend, choose the closest day as well.
        Calendar july4Calendar = Calendar.getInstance();
        july4Calendar.set(year, Calendar.JULY, 4);
        
        holidays.add(getHolidayStruct(Calendar.JULY, 4, year));
        
        if (july4Calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            holidays.add(getHolidayStruct(Calendar.JULY, 5, year));
        }

        if (july4Calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            holidays.add(getHolidayStruct(Calendar.JULY, 3, year));
        }
        

        // Labor Day
        Calendar laborDayCalendar = Calendar.getInstance();
        laborDayCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        laborDayCalendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, 1);
        laborDayCalendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
        laborDayCalendar.set(Calendar.YEAR, year);
        holidays.add(getHolidayStruct(Calendar.SEPTEMBER, laborDayCalendar.get(Calendar.DATE), year));


        return holidays;
    }

    public Map<String,Integer> getHolidayStruct(int month, int day, int year) {
        Map<String,Integer> holidayStruct = new HashMap<>();
        holidayStruct.put("month", month);
        holidayStruct.put("day", day);
        holidayStruct.put("year", year);
        return holidayStruct;
    }

    public boolean isDateOnAHoliday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        List<Map<String,Integer>> holidays = getHolidaysForYear(cal.get(Calendar.YEAR));

        for (Map<String,Integer> holiday : holidays) {
            // If monnth & day match. It's a holiday. We have already accounted for wierd days and adding this to the list.
            if (holiday.get("month") == cal.get(Calendar.MONTH) && holiday.get("day") == cal.get(Calendar.DATE)) {
                return true;
            }
        }
        return false;
    }

    public double calculateTotalForCart(Cart cart) {
        double rentalFees = calculateRentalFeesForCart(cart);
        double discountAmount = rentalFees * cart.getDiscountPercent();
        return rentalFees - discountAmount;
    }

    public float calculateDiscountAmountForCart(Cart cart) {
        double rentalFees = calculateRentalFeesForCart(cart);
        double discountAmount = rentalFees * cart.getDiscountPercent();
        return Math.round(discountAmount * 100) / 100;
    }

    public double calculateRentalFeesForCart(Cart cart) {
        double dailyChargesAggregate = 0;

        // Get Products & Days Individually.
       for (CartItem item : cart.getItems()) {
           // Get chargable days for cart.
            Product curProduct = findProductByCode(item.getToolCode());
            int chargableDaysForProduct = getChargableDaysForProductForDaysStarting(curProduct, item.getRentalDays(), cart.getCheckoutDate());
            dailyChargesAggregate = dailyChargesAggregate + (chargableDaysForProduct * curProduct.getCharges_daily());
       }

        return dailyChargesAggregate;
    }

    public int getChargableDaysForProductForDaysStarting(Product product, int daysToRent, Date checkoutDate) {
        int chargableDays = 0;
        for(int i = 0; i < daysToRent; i++) {
            // Add one day until no more
            Calendar cal = Calendar.getInstance();
            cal.setTime(checkoutDate);
            cal.add(Calendar.DATE, i);

            boolean isCurrentDateAWeekday = cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY; 
            boolean isCurrentDateAWeekEnd = !isCurrentDateAWeekday;
            boolean isCurrentDateAHoliday = isDateOnAHoliday(checkoutDate);

            boolean productChargableWeekDay =  product.getCharges_weekday() == 1 ? true : false;
            boolean productChargableWeekEnd =  product.getCharges_weekend() == 1 ? true : false;
            boolean productChargableHoliday =  product.getCharges_holiday() == 1 ? true : false;
            double productCostPerDay = product.getCharges_daily();

            // If it's a weekday & this product doesn't charge for weekdays move on.
            if (isCurrentDateAWeekday && !productChargableWeekDay) {
                continue;
            }
             
            // If it's a weekend & this product doesn't charge for weekends, move on.
            if (isCurrentDateAWeekEnd && !productChargableWeekEnd) {                
                continue;
            }

            // If it's a holiday & this product doesn't charge for holiday's, move on.
            if (isCurrentDateAHoliday && !productChargableHoliday) {                
                continue;
            }

            chargableDays++;

            // This way. Customer only needs 1 excuse to not get charged.

        }
        return chargableDays;
    }

    public Product findProductByCode(String tool_code) {
        Product searcher = new Product();
        searcher.setTool_code(tool_code);
        Example<Product> productExample = Example.of(searcher);
        Optional<Product> result = productRepository.findOne(productExample);
        return result.isEmpty() ? null : result.get();
    }
}
