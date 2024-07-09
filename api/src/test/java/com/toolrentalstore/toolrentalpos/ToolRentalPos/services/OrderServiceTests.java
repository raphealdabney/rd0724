package com.toolrentalstore.toolrentalpos.ToolRentalPos.services;

import java.time.Instant;
import java.util.Date;

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
        Product product = new Product(1, "test prod", 1.0f, 1,1,1,"JAKD", "jackhammer","dewalt", "");
        int daysToRent = 4;
        Date checkoutDate = Date.from(Instant.parse("2024-07-01T00:00:00.00Z"));
        int chargableDays = orderService.getChargableDaysForProductForDaysStarting(product, daysToRent, checkoutDate);
        assertEquals(5, chargableDays);
    }

}
