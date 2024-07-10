package com.toolrentalstore.toolrentalpos.ToolRentalPos.Services;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.toolrentalstore.toolrentalpos.ToolRentalPos.Models.Cart;
import com.toolrentalstore.toolrentalpos.ToolRentalPos.Models.CartItem;
import com.toolrentalstore.toolrentalpos.ToolRentalPos.Models.Order;
import com.toolrentalstore.toolrentalpos.ToolRentalPos.Models.OrderItem;
import com.toolrentalstore.toolrentalpos.ToolRentalPos.Models.Product;
import com.toolrentalstore.toolrentalpos.ToolRentalPos.Models.RentalAgreement;
import com.toolrentalstore.toolrentalpos.ToolRentalPos.Repositories.OrderItemRepository;
import com.toolrentalstore.toolrentalpos.ToolRentalPos.Repositories.OrderRepository;
import com.toolrentalstore.toolrentalpos.ToolRentalPos.Repositories.ProductRepository;

@Service
public class OrderService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    public OrderService(ProductRepository productRepository, OrderItemRepository orderItemRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
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
        double discountAmount = rentalFees * (double) (cart.getDiscountPercent() / 100.0f);
        System.out.println("--");
        System.out.println("Discount Percentage: " + String.valueOf(cart.getDiscountPercent()));
        System.out.println("Discount Amount: " + String.valueOf(discountAmount));
        System.out.println("Rental Fees: " + String.valueOf(rentalFees));
        return rentalFees - discountAmount;
    }

    public float calculateDiscountAmountForCart(Cart cart) {
        double rentalFees = calculateRentalFeesForCart(cart);
        double discountAmount = rentalFees * (double) (cart.getDiscountPercent() / 100.0f);
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

    public List<String> validateCart(Cart cart) {
        List<String> output = new ArrayList<>();
        List<String> running = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            // Rental days error.
            if (item.getRentalDays() < 1) {
                running.add("Product with Tool Code: " + item.getToolCode() + ", must be reserved for a minimum of 1 day.");
            }

            // Discount error.
            if (cart.getDiscountPercent() < 0  || cart.getDiscountPercent() > 100) {
                running.add("Your discount must be between 0 and 100%.");
            }
        }
        if (!running.isEmpty()) {
            output.add("Sorry, there has been an error.");
            output.addAll(running);
            running.clear();
        }
        return output;
    }

    public String processOrder(Cart cart) throws Exception {
        String output = "";

        // Validate Order.
        List<String> errors = validateCart(cart);
        if (!errors.isEmpty()) {
            String errs = "";
            for (String error : errors) {
                errs.concat(error+"\n");
            }
            throw new Exception(errs);
        }

        // Create Order from cart.
        Order order = orderRepository.save(new Order(cart.getCheckoutDate()));
        System.out.println("--");
        System.out.println("Order Created with id " + order.getId());

        // Create Order Items.
        for (CartItem item : cart.getItems()) {
            OrderItem oItem = orderItemRepository.save(new OrderItem(item.getToolCode(), item.getRentalDays(), cart.getDiscountPercent(), order.getId()));
            System.out.println("Order Item created for tool: " + oItem.getTool_code());
        }

        // Produce rental output.
        RentalAgreement rentalAgreement = RentalAgreement.produceFromCart(cart, order, this);

        // Generate command output.
        outputRentalAgreementToSys(rentalAgreement);

        // Generate HTML output.
        output = createRentalAgreementHTML(rentalAgreement, cart);
        return output;
    }


    public void outputRentalAgreementToSys(RentalAgreement rentalAgreement) {
        System.out.println("-- Order #" + rentalAgreement.getOrderId() +  " Processed --");
        System.out.println("Tool Code: " + rentalAgreement.getToolCode());
        System.out.println("Tool Type: " + rentalAgreement.getToolType()); // ● Tool type - From tool info
        System.out.println("Tool brand: " + rentalAgreement.getToolBrand()); // ●  From tool info
        System.out.println("Rental days: " + rentalAgreement.getRentalDays()); // ●  Specified at checkout
        System.out.println("Check out date: " + formatDate(rentalAgreement.getCheckoutDate())); // ●  - Specified at checkout
        System.out.println("Due date: " + formatDate(rentalAgreement.getDueDate())); // ●  Calculated from checkout date and rental days.
        System.out.println("Daily rental char: " + formatCurrency(rentalAgreement.getDailyCharge())); // ●  - Amount per day, specified by the tool type.
        System.out.println("Charge days: " + rentalAgreement.getChargeDays()); // ●  Count of chargeable days, from day after checkout through and including due
        System.out.println("Pre-discount Charge : " + formatCurrency(rentalAgreement.getPreDiscountCharge())); // ● Pre-discount charge - Calculated as charge days X daily charge. Resulting total rounded half up
        System.out.println("Discount Percent: " + rentalAgreement.getDiscountPercent() + "%"); // ● Discount percent - Specified at checkout.
        System.out.println("Discount Amount: " + formatCurrency(rentalAgreement.getDiscountAmount())); // ● Discount amount - calculated from discount % and pre-discount charge. Resulting amount
        System.out.println("Final Charge: " + formatCurrency(rentalAgreement.getFinalCharge())); // ● Final charge - Calculated as pre-discount charge - discount amount.
    }

    public String createRentalAgreementHTML(RentalAgreement rentalAgreement, Cart cart) {
        String output = "<div class=\"text-center\">\n" + //
                        "          <img src=\"assets/img/receipt-logo.png\" alt=\"Tool Rental POS\" class=\"mb-3 w-8 h-8 inline-block\">\n" + //
                        "          <h2 class=\"text-xl font-semibold\">Rental Agreement</h2>\n" + //
                        "          <p>Tool Rental Store</p>\n" + //
                        "        </div>\n" + //
                        "        <div class=\"flex mt-4 text-xs\">\n" + //
                        "          <div class=\"flex-grow\">No: <span x-text=\"receiptNo\">" + rentalAgreement.getOrderId() + "</span></div>\n" + //
                        "          <div x-text=\"receiptDate\">" + formatDate(rentalAgreement.getCheckoutDate()) + "</div>\n" + //
                        "        </div>\n" + //
                        "        <hr class=\"my-2\">\n" + //
                        "        <div>\n" + //
                        "          <table class=\"w-full text-xs\">\n" + //
                        "            <thead>\n" + //
                        "             <tr>\n" + //
                        "                <th class=\"py-1 w-1/12 text-center\">#</th>\n" + //
                        "                <th class=\"py-1 text-left\">Item</th>\n" + //
                        "                <th class=\"py-1 w-2/12 text-center\">Qty</th>\n" + //
                        "                <th class=\"py-1 w-3/12 text-right\">Subtotal</th>\n" + //
                        "             </tr>\n" + //
                        "            </thead>\n" + //
                        "            <tbody>\n";

                        // Loop items.
                        for (CartItem item : cart.getItems()) {
                            Product prod = findProductByCode(item.getToolCode());
                            int chargeDays = getChargableDaysForProductForDaysStarting(prod, item.getRentalDays(), cart.getCheckoutDate());
                            output = output.concat("                <tr>\n" + //
                            "                  <td class=\"py-2 text-center\" x-text=\"index+1\">" + item.getToolCode() + "</td>\n" + //
                            "                  <td class=\"py-2 text-left\">\n" + //
                            "                    <span x-text=\"item.name\">" + prod.getName() + "</span>\n" + //
                            "                    <br/>\n" + //
                            "                    <small x-text=\"priceFormat(item.price)\">" + prod.getCharges_daily()  + " / day</small>\n" + //
                            "                  </td>\n" + //
                            "                  <td class=\"py-2 text-center\" x-text=\"item.qty\">" + chargeDays + "</td>\n" + //
                            "                  <td class=\"py-2 text-right\" x-text=\"priceFormat(item.qty * item.price)\">" + (chargeDays*prod.getCharges_daily()) + "</td>\n" + //
                            "                </tr>\n"
                            );
                        }


                        output = output.concat("            </tbody>\n" + //
                        "          </table>\n" + //
                        "        </div>\n" + //
                        "        <hr class=\"my-2\">\n" + //
                        "        <div>\n" + //
                        "          <div class=\"flex font-semibold\">\n" + //
                        "            <div class=\"flex-grow\">TOTAL</div>\n" + //
                        "            <div>" + formatCurrency(rentalAgreement.getFinalCharge()) + "</div>\n" + //
                        "          </div>\n" + //
                        "          <hr class=\"my-2\">\n" + //
                        "        </div>");


        return output;
    }

    public int getChargableDaysForProductForDaysStarting(Product product, int daysToRent, Date checkoutDate) {
        int chargableDays = 0;
        Calendar initCal = Calendar.getInstance();
        initCal.setTime(checkoutDate);
        int initDate = initCal.get(Calendar.DATE);
        for(int i = 0; i < daysToRent; i++) {
            // Add one day until no more
            Calendar cal = Calendar.getInstance();
            cal.setTime(checkoutDate);
            cal.set(Calendar.DATE, i + initDate);

            boolean isCurrentDateAWeekday = cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY; 
            boolean isCurrentDateAWeekEnd = !isCurrentDateAWeekday;
            boolean isCurrentDateAHoliday = isDateOnAHoliday(cal.getTime());

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
       List<Product> products = productRepository.findAll();
       for (Product prod : products) {
            if (prod.getTool_code().equals(tool_code)) {
                return prod;
            }
       }
       return null;
    }

    // Format Date according to this format :  Date mm/dd/yy
    public String formatDate(Date date) {
        String output = "";        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yy");
        output = simpleDateFormat.format(date);
        return output;
    }

    // Format currency according to this format :  $9,999.99
    public String formatCurrency(double amount) {
        String output = "";
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        output = formatter.format(amount);
        return output;
    }

    public String formatStrList(Stream<String> stringList) {
        String output = stringList.reduce("", (codes, item) -> codes + (codes.length() > 0 ? ", " : "") + item);
        return output;
    }
}
