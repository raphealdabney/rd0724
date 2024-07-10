package com.toolrentalstore.toolrentalpos.ToolRentalPos.Models;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "shop_order_item")
public class OrderItem {

    public OrderItem(Integer id, String tool_code, Integer rental_day_count, Integer discount_percentage, Integer shop_order_id) {
        this.id = id;
        this.tool_code = tool_code;
        this.rental_day_count = rental_day_count;
        this.discount_pertcentage = discount_percentage;
        this.shop_order_id = shop_order_id;    
    }
    public OrderItem(String tool_code, Integer rental_day_count, Integer discount_percentage, Integer shop_order_id) {
        this.tool_code = tool_code;
        this.rental_day_count = rental_day_count;
        this.discount_pertcentage = discount_percentage;
        this.shop_order_id = shop_order_id;    
    }

    public OrderItem() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "tool_code")
    private String tool_code;

    @Column(name = "rental_day_count")
    private Integer rental_day_count;

    @Column(name = "discount_percentage")
    private Integer discount_pertcentage;

    @Column(name = "shop_order_id")
    private Integer shop_order_id;
    

    /**
     * @return Integer return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return String return the tool_code
     */
    public String getTool_code() {
        return tool_code;
    }

    /**
     * @param tool_code the tool_code to set
     */
    public void setTool_code(String tool_code) {
        this.tool_code = tool_code;
    }

    /**
     * @return Integer return the rental_day_count
     */
    public Integer getRental_day_count() {
        return rental_day_count;
    }

    /**
     * @param rental_day_count the rental_day_count to set
     */
    public void setRental_day_count(Integer rental_day_count) {
        this.rental_day_count = rental_day_count;
    }

    /**
     * @return Integer return the discount_pertcentage
     */
    public Integer getDiscount_pertcentage() {
        return discount_pertcentage;
    }

    /**
     * @param discount_pertcentage the discount_pertcentage to set
     */
    public void setDiscount_pertcentage(Integer discount_pertcentage) {
        this.discount_pertcentage = discount_pertcentage;
    }

    /**
     * @return Integer return the shop_order_id
     */
    public Integer getShop_order_id() {
        return shop_order_id;
    }

    /**
     * @param shop_order_id the shop_order_id to set
     */
    public void setShop_order_id(Integer shop_order_id) {
        this.shop_order_id = shop_order_id;
    }

}