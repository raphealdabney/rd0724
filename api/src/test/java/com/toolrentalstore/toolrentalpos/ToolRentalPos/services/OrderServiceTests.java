package com.toolrentalstore.toolrentalpos.ToolRentalPos.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.toolrentalstore.toolrentalpos.ToolRentalPos.Models.Cart;
import com.toolrentalstore.toolrentalpos.ToolRentalPos.Models.CartItem;
import com.toolrentalstore.toolrentalpos.ToolRentalPos.Models.Order;
import com.toolrentalstore.toolrentalpos.ToolRentalPos.Models.Product;
import com.toolrentalstore.toolrentalpos.ToolRentalPos.Models.RentalAgreement;
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

    @Test
    public void processOrder() throws Exception {
        Product product = new Product(1, "test prod", 1.0f, 1,1,0,"JAKD", "jackhammer","dewalt", "");
        int daysToRent = 4;
        Calendar checkoutDate = Calendar.getInstance();
        checkoutDate.set(2024,Calendar.JULY,1);

        Cart cart = new Cart();
        CartItem item = new CartItem(product.getTool_code(), daysToRent);
        List<CartItem> items = new ArrayList<>();
        items.add(item);
        cart.setItems(items);
        cart.setCheckoutDate(checkoutDate.getTime());
        cart.setDiscountPercent(50);

        Order order = new Order();
        order.setId(99);
        RentalAgreement rentalAgreement = RentalAgreement.produceFromCart(cart, order, orderService);
        orderService.processOrder(cart);


        //add another
        items.add(new CartItem("LADW", 8));
        order.setId(88);
        cart.setItems(items);
        RentalAgreement ra2 = RentalAgreement.produceFromCart(cart, order, orderService);
        orderService.processOrder(cart);
    }

}
