package com.teamviewer.remotedesktopshop.RemoteDesktopShop.Models;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "shop_order")
public class Order {
    public Order(Integer id, Date date, String token) {
        this.id = id;
        this.date = date;
        this.token = token;
    }

    public Order() {

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "date")
    private Date date;

    @Column(name = "token")
    private String token;

}
