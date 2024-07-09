package com.toolrentalstore.toolrentalpos.ToolRentalPos.Models;

import jakarta.persistence.*;

@Entity
@Table(name = "product")
public class Product {
    public Product() {
    }

    public Product(Integer id, String name, float charges_daily, Integer charges_weekday, Integer charges_weekend, Integer charges_holiday, String tool_code, String tool_type, String brand, String image) {
        this.id = id;
        this.name = name;
        this.charges_daily = charges_daily;
        this.charges_weekday = charges_weekday;
        this.charges_weekend = charges_weekend;
        this.charges_holiday = charges_holiday;
        this.tool_type = tool_type;
        this.tool_code = tool_code;
        this.image = image;
        this.brand = brand;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name="charges_daily")
    private float charges_daily;

    @Column(name = "charges_weekday")
    private Integer charges_weekday;
    
    @Column(name = "charges_weekend")
    private Integer charges_weekend;

    @Column(name = "charges_holiday")
    private Integer charges_holiday;

    @Column(name = "tool_code")
    private String tool_code;

    @Column(name = "tool_type")
    private String tool_type;
    
    @Column(name = "image")
    private String image;
    
    @Column(name = "brand")
    private String brand;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return float return the charges_daily
     */
    public float getCharges_daily() {
        return charges_daily;
    }

    /**
     * @param charges_daily the charges_daily to set
     */
    public void setCharges_daily(float charges_daily) {
        this.charges_daily = charges_daily;
    }

    /**
     * @return Integer return the charges_weekday
     */
    public Integer getCharges_weekday() {
        return charges_weekday;
    }

    /**
     * @param charges_weekday the charges_weekday to set
     */
    public void setCharges_weekday(Integer charges_weekday) {
        this.charges_weekday = charges_weekday;
    }

    /**
     * @return Integer return the charges_weekend
     */
    public Integer getCharges_weekend() {
        return charges_weekend;
    }

    /**
     * @param charges_weekend the charges_weekend to set
     */
    public void setCharges_weekend(Integer charges_weekend) {
        this.charges_weekend = charges_weekend;
    }

    /**
     * @return Integer return the charges_holiday
     */
    public Integer getCharges_holiday() {
        return charges_holiday;
    }

    /**
     * @param charges_holiday the charges_holiday to set
     */
    public void setCharges_holiday(Integer charges_holiday) {
        this.charges_holiday = charges_holiday;
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
     * @return String return the tool_type
     */
    public String getTool_type() {
        return tool_type;
    }

    /**
     * @param tool_type the tool_type to set
     */
    public void setTool_type(String tool_type) {
        this.tool_type = tool_type;
    }

    /**
     * @return String return the brand
     */
    public String getBrand() {
        return brand;
    }

    /**
     * @param brand the brand to set
     */
    public void setBrand(String brand) {
        this.brand = brand;
    }


    /**
     * @return String return the image
     */
    public String getImage() {
        return image;
    }

    /**
     * @param image the image to set
     */
    public void setImage(String image) {
        this.image = image;
    }

}
