package com.toolrentalstore.toolrentalpos.ToolRentalPos.Models;

import java.util.Calendar;
import java.util.Date;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import com.toolrentalstore.toolrentalpos.ToolRentalPos.Services.OrderService;

/*
 * Model to hold / reference data related with Rental Agreements in a consistant way.
 */
public class RentalAgreement {

    private double preDiscountCharge;
    private Integer discountPercent;
    private double discountAmount;
    private double finalCharge;
    private String toolCode;
    private String toolType;
    private String brand;
    private Integer rentalDays;
    private double dailyCharge;
    private Integer chargeDays;
    private Date dueDate;
    private Date checkoutDate;
    private int orderId;

    public RentalAgreement() {
        
    }
    public RentalAgreement(double preDiscountCharge,    Integer discountPercent,    double discountAmount,    double finalCharge,    String toolCode,    String toolType,    String brand,    Integer rentalDays,    double dailyCharge,    Integer chargeDays,    Date dueDate,    Date checkoutDate) {
        this.preDiscountCharge = preDiscountCharge;
        this.discountPercent = discountPercent;
        this.discountAmount = discountAmount;
        this.finalCharge = finalCharge;
        this.toolCode = toolCode;
        this.toolType = toolType;
        this.brand = brand;
        this.rentalDays = rentalDays;
        this.dailyCharge = dailyCharge;
        this.chargeDays = chargeDays;
        this.dueDate = dueDate;
        this.checkoutDate = checkoutDate;
    }

    /*
     * Method to product a rental agreement object give a cart, order ,and pass-through orderservice handle.
     */
    public static RentalAgreement produceFromCart(Cart cart, Order order, OrderService orderService) {
        RentalAgreement ra = new RentalAgreement();
        ra.setOrderId(order.getId());
        ra.setDiscountPercent(cart.getDiscountPercent());

        // Aggregate the multiple items in a cart into a printing comma separated list or added numbers.
        Stream<String> toolCodeAgg = cart.getItems().stream().map(e -> e.getToolCode());
        Stream<String> toolTypeAgg = cart.getItems().stream().map(e -> orderService.findProductByCode(e.getToolCode()).getTool_type());
        Stream<String> toolBrandAgg = cart.getItems().stream().map(e -> orderService.findProductByCode(e.getToolCode()).getBrand());
        Stream<Integer> rentalDaysAgg = cart.getItems().stream().map(e -> e.getRentalDays());
        Stream<Float> dailyCharAgg = cart.getItems().stream().map(e -> orderService.findProductByCode(e.getToolCode()).getCharges_daily());
        Stream<Integer> chargeDaysAgg = cart.getItems().stream().map(e -> orderService.getChargableDaysForProductForDaysStarting(orderService.findProductByCode(e.getToolCode()), e.getRentalDays(), cart.getCheckoutDate()));

        double combinedDailyCharges = dailyCharAgg.reduce(0.0f,(agg, i)->i+agg);
        int combinedChargeDays = chargeDaysAgg.reduce(0, (agg,i) -> i+agg);
        int maxRentalDays = rentalDaysAgg.reduce(0, (agg,i) -> i > agg ? i : agg);

        // Calc due date.
        Calendar cal = Calendar.getInstance();
        cal.setTime(cart.getCheckoutDate());
        cal.add(Calendar.DATE, maxRentalDays);

        // properties.
        ra.setToolCode(orderService.formatStrList(toolCodeAgg));
        ra.setToolType(orderService.formatStrList(toolTypeAgg));
        ra.setBrand(orderService.formatStrList(toolBrandAgg));
        ra.setRentalDays(maxRentalDays);
        ra.setDailyCharge(combinedDailyCharges);
        ra.setChargeDays(combinedChargeDays);
        ra.setDueDate(cal.getTime());
        ra.setFinalCharge(orderService.calculateTotalForCart(cart));
        ra.setPreDiscountCharge(orderService.calculateRentalFeesForCart(cart));
        ra.setCheckoutDate(cart.getCheckoutDate());
        ra.setDiscountAmount(orderService.calculateDiscountAmountForCart(cart));

        return ra;
    }
    

  /**
     * @return double return the preDiscountCharge
     */
    public double getFinalCharge() {
        return finalCharge;
    }

    /**
     * @param finalCharge the preDiscountCharge to set
     */
    public void setFinalCharge(double finalCharge) {
        this.finalCharge = finalCharge;
    }

    /**
     * @return double return the preDiscountCharge
     */
    public double getPreDiscountCharge() {
        return preDiscountCharge;
    }

    /**
     * @param preDiscountCharge the preDiscountCharge to set
     */
    public void setPreDiscountCharge(double preDiscountCharge) {
        this.preDiscountCharge = preDiscountCharge;
    }

    /**
     * @return double return the discountPercent
     */
    public Integer getDiscountPercent() {
        return discountPercent;
    }

    /**
     * @param discountPercent the discountPercent to set
     */
    public void setDiscountPercent(Integer discountPercent) {
        this.discountPercent = discountPercent;
    }

    /**
     * @return double return the discountAmount
     */
    public double getDiscountAmount() {
        return discountAmount;
    }

    /**
     * @param discountAmount the discountAmount to set
     */
    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    /**
     * @param toolCode the toolCode to set
     */
    public void setToolCode(String toolCode) {
        this.toolCode = toolCode;
    }

    /**
     * @return String return the toolType
     */
    public String getToolType() {
        return toolType;
    }

    /**
     * @param toolType the toolType to set
     */
    public void setToolType(String toolType) {
        this.toolType = toolType;
    }

    /**
     * @param brand the brand to set
     */
    public void setBrand(String brand) {
        this.brand = brand;
    }

    /**
     * @param rentalDays the rentalDays to set
     */
    public void setRentalDays(Integer rentalDays) {
        this.rentalDays = rentalDays;
    }

    /**
     * @return double return the dailyCharge
     */
    public double getDailyCharge() {
        return dailyCharge;
    }

    /**
     * @param dailyCharge the dailyCharge to set
     */
    public void setDailyCharge(double dailyCharge) {
        this.dailyCharge = dailyCharge;
    }

    /**
     * @return Integer return the chargeDays
     */
    public Integer getChargeDays() {
        return chargeDays;
    }

    /**
     * @param chargeDays the chargeDays to set
     */
    public void setChargeDays(Integer chargeDays) {
        this.chargeDays = chargeDays;
    }

    /**
     * @return Date return the dueDate
     */
    public Date getDueDate() {
        return dueDate;
    }

    /**
     * @param dueDate the dueDate to set
     */
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * @param checkoutDate the checkoutDate to set
     */
    public void setCheckoutDate(Date checkoutDate) {
        this.checkoutDate = checkoutDate;
    }


    /**
     * @return int return the orderId
     */
    public int getOrderId() {
        return orderId;
    }

    /**
     * @param orderId the orderId to set
     */
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    public String getToolBrand() {
        return brand;
    }
    public String getToolCode() {
        return toolCode;
    }
    public Integer getRentalDays() {
        return rentalDays;
    }
    public Date getCheckoutDate() {
        return checkoutDate;
    }

}
