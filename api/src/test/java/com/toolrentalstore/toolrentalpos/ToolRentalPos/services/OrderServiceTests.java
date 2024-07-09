package com.toolrentalstore.toolrentalpos.ToolRentalPos.services;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.toolrentalstore.toolrentalpos.ToolRentalPos.Models.Product;
import com.toolrentalstore.toolrentalpos.ToolRentalPos.Services.OrderService;

@SpringBootTest
class OrderServiceTests {

    @Autowired
    private OrderService orderService;

    @Test
    public void getChargableDaysForProductForDaysStarting() {
        // Integer id, String name, float charges_daily, Integer charges_weekday, Integer charges_weekend, Integer charges_holiday, String tool_code, String tool_type, String brand, String image
        Product product = new Product(1, "test prod", 1.0f, 1,1,0,"JAKD", "jackhammer","dewalt", "");
        int daysToRent = 4;
        Calendar checkoutDate = Calendar.getInstance();
        checkoutDate.set(2024,Calendar.JULY,1);
        int chargableDays = orderService.getChargableDaysForProductForDaysStarting(product, daysToRent, checkoutDate.getTime());
        assertEquals(3, chargableDays);
    }

}
