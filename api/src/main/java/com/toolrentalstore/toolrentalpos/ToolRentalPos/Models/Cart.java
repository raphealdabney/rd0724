package com.toolrentalstore.toolrentalpos.ToolRentalPos.Models;

import java.util.Date;
import java.util.List;

public class Cart {
    
    private int discountPercent;
    private List<CartItem> items;
    private Date checkoutDate;

    
	public Cart(List<CartItem> items, Date checkoutDate, int discountPercent) {
        this.items = items;
        this.checkoutDate = checkoutDate;
        this.discountPercent = discountPercent;
    }
    public Cart(List<CartItem> items, Date checkoutDate) {
        this(items, checkoutDate, 0);    
    }

    public Cart() {
        
    }


    /**
     * @return int return the discountPercent
     */
    public int getDiscountPercent() {
        return discountPercent;
    }

    /**
     * @param discountPercent the discountPercent to set
     */
    public void setDiscountPercent(int discountPercent) {
        this.discountPercent = discountPercent;
    }

    /**
     * @return List<CartItem> return the items
     */
    public List<CartItem> getItems() {
        return items;
    }

    /**
     * @param items the items to set
     */
    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    /**
     * @return Date return the checkoutDate
     */
    public Date getCheckoutDate() {
        return checkoutDate;
    }

    /**
     * @param checkoutDate the checkoutDate to set
     */
    public void setCheckoutDate(Date checkoutDate) {
        this.checkoutDate = checkoutDate;
    }

}
