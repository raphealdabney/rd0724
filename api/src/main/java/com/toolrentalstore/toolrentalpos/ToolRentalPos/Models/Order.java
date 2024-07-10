package com.toolrentalstore.toolrentalpos.ToolRentalPos.Models;

import jakarta.persistence.*;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Calendar;

@Entity
@Table(name = "shop_order")
public class Order {
    public Order(int id, Date date2) {
        this.id = id;
        this.date = date2;
    }

    public Order() {

    }

    public Order(java.util.Date checkoutDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(checkoutDate);
        this.date = Date.valueOf(LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "date")
    private Date date;


}
