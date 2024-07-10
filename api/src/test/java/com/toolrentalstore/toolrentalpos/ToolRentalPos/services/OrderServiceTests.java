package com.toolrentalstore.toolrentalpos.ToolRentalPos.services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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

    /*
     * Test to confirm the core logic in calculating the chargable days 
     * taking into account holiday rules & product specific weekday / weekend charges.
     */
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

    /*
     * Test to process the orders originating from REST API / and or other places where we want to store the data in db.
     */
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


    /*
     * Test to validate the scenarios provided in the challenge.
     */
    @Test
    public void testRentalScenarios() {
        // Create list of scenarios to test.
        List<Map<String,String>> scenarios = new ArrayList<>();
        scenarios.add(Map.of("tool_code","JAKR","checkout_date","9/03/15","rental_days","5","discount","101","assert_due","---","assert_final","---"));
        scenarios.add(Map.of("tool_code","LADW","checkout_date","7/2/20","rental_days","3","discount","10","assert_due","07/05/20","assert_final","$1.79"));
        scenarios.add(Map.of("tool_code","CHNS","checkout_date","7/2/15","rental_days","5","discount","25","assert_due","07/07/15","assert_final","$3.35"));
        scenarios.add(Map.of("tool_code","JAKD","checkout_date","9/3/15","rental_days","6","discount","0","assert_due","09/09/15","assert_final","$8.97"));
        scenarios.add(Map.of("tool_code","JAKR","checkout_date","7/2/15","rental_days","9","discount","0","assert_due","07/11/15","assert_final","$17.94"));
        scenarios.add(Map.of("tool_code","JAKR","checkout_date","7/2/20","rental_days","4","discount","50","assert_due","07/06/20","assert_final","$1.50"));

        // Create products for test.
        Product jakrProduct = new Product(1, "Jackhammer R", 2.99f, 1,0,0,"JAKR", "jackhammer","ridgid","");
        Product jakdProduct = new Product(1, "Jackhammer D", 2.99f, 1,0,0,"JAKD", "jackhammer","dewalt","");
        Product ladderProduct = new Product(1, "Ladder", 1.99f, 1,1,0,"LADW", "ladder","werner","");
        Product chainsawProduct = new Product(1, "Chainsaw", 1.49f, 1,0,1,"CHNS", "chainsaw","stihl","");

        // Loop through all scenarios.
        int ind = 0;
        for (Map<String,String> scenario : scenarios) {
            ind++;
            // Create Cart Item.
            CartItem cartItem = new CartItem(scenario.get("tool_code"), Integer.valueOf(scenario.get("rental_days")));

            // Get Date Object.
            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy");
            Date coDate = null;
            try {
                coDate = df.parse(scenario.get("checkout_date"));
            } catch(Exception e) {
                fail("Date from scenario cannot be parsed in scenario " + ind + ".");
            }

            // Create Cart with list of items
            List<CartItem> items = new ArrayList<>();
            items.add(cartItem);
            Cart cart = new Cart();
            cart.setItems(items);
            cart.setCheckoutDate(coDate);
            cart.setDiscountPercent(Integer.valueOf(scenario.get("discount")));

            // Create order for id.
            Order order = new Order();
            order.setId(99);

            // Check validity of cart. This happens through the "processOrder" method naturally, however it's outside the scope of the challenge to mock a db save method, etc. . .
            // So I will just invoke the method that is used to raise the exception here.
            // It also checks if any exception is raised from any scenario that we don't expect the test will catch it here and fail the test.
            boolean validCart = true;
            try {
                orderService.validateCart(cart,true);
            } catch(Exception e) {
                validCart = false;
            }

            if (!validCart) {
                if (scenario.get("assert_due").equals("---")) {
                    assertTrue(true, "Scenario triggered exception " + ind);
                    continue;
                } else {
                    fail("Cart is not valid in scenario " + ind);
                }
            }
            

            // Create rental agreement.
            RentalAgreement rentalAgreement = RentalAgreement.produceFromCart(cart, order, orderService);

            // output to system.
            String result = orderService.outputRentalAgreementToSys(rentalAgreement);

            // assert returned string contains the following.
            // These assertions are testing the actual string output for the proper values with appropriate formatting.
            assertTrue(result.contains(scenario.get("assert_due")), "scenario due date failed" + ind);
            assertTrue(result.contains(scenario.get("assert_final")), "scenario final price failed " + ind);

            // @todo: use the rentalAgreement object to validate the computed values individually for accuracy.
        }
        
        

        

    }
}
