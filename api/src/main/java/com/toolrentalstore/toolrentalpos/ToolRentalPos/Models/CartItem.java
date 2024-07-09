package com.toolrentalstore.toolrentalpos.ToolRentalPos.Models;

public class CartItem {
    
    private int rentalDays;
    private String toolCode;

    public CartItem() {
        
    }

    public CartItem(String toolCode, int rentalDays) {
        this.toolCode = toolCode;
        this.rentalDays = rentalDays;
    }



    /**
     * @return int return the rentalDays
     */
    public int getRentalDays() {
        return rentalDays;
    }

    /**
     * @param rentalDays the rentalDays to set
     */
    public void setRentalDays(int rentalDays) {
        this.rentalDays = rentalDays;
    }

    /**
     * @return String return the toolCode
     */
    public String getToolCode() {
        return toolCode;
    }

    /**
     * @param toolCode the toolCode to set
     */
    public void setToolCode(String toolCode) {
        this.toolCode = toolCode;
    }

}
