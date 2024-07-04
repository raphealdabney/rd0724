package com.teamviewer.remotedesktopshop.RemoteDesktopShop.Controllers;

import com.teamviewer.remotedesktopshop.RemoteDesktopShop.Models.Order;
import com.teamviewer.remotedesktopshop.RemoteDesktopShop.Repositories.OrderRepository;
import org.aspectj.weaver.ast.Or;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.RequestBody;

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ShopOrderControllerTest {

    @Autowired
    private ShopOrderController controller;
    @Autowired
    private OrderRepository repository;

    @AfterEach
    void tearDown() {
    }

    @Test
    void findAll() {
        // Find all and take count.
        List<Order> orders = (List<Order>) controller.findAll();
        int initialCount = orders.size();

        // Create Shop Order.
        Order order1 = new Order(null, Date.valueOf("2024-01-02"), "hey");
        repository.save(order1);

        // Create shop order.
        Order order2 = new Order(null, Date.valueOf("2024-01-02"), "hey");
        repository.save(order2);

        // findall and see that count delta is == 2
        orders = (List<Order>) controller.findAll();
        int afterCount = orders.size();
        assertEquals(2, afterCount - initialCount);

        // clean up
        repository.delete(order1);
        repository.delete(order2);

    }

    @Test
    void findOne() {
        // Create Shop Order.
        Order order1 = new Order(null, Date.valueOf("2024-01-02"), "hey");
        repository.save(order1);

        // Create shop order.
        Order order2 = new Order(null, Date.valueOf("2024-01-02"), "there");
        repository.save(order2);

        // Find one using controller.
        Order order = controller.findOne(order2.getId());
        assertEquals("there", order.getToken());

        // Clean up.
        repository.delete(order1);
        repository.delete(order2);
    }

    @Test
    void createOne() {
        Order order1 = new Order();
        order1.setToken("whoa");
        List<Order> orders = (List<Order>) repository.findAll();
        int initCount = orders.size();

        Order newOrder = controller.createOne(order1);
        orders = (List<Order>) repository.findAll();
        assertEquals(1, orders.size() - initCount);

        // Clean Up.
        repository.delete(newOrder);
    }

    @Test
    void updateOne() {
        // Create Order.
        Order order1 = new Order();
        order1.setToken("whatwhat");
        Order savedOrder = repository.save(order1);

        // Update Order.
        Order newOrderData = new Order(null,Date.valueOf("2024-02-01"), "changedd");
        Order newSavedOrder = controller.updateOne(savedOrder.getId(), newOrderData);
        assertEquals("changedd", newSavedOrder.getToken());

        // Clean up.
        repository.delete(newSavedOrder);
    }

    @Test
    void deleteOne() {
        // Create order
        Order order1 = new Order();
        order1.setToken("whoa");

        // Get count before adding.
        List<Order> orders = (List<Order>) repository.findAll();
        int initCount = orders.size();

        // Add order then assert it went up by one.
        Order newOrder = repository.save(order1);
        orders = (List<Order>) repository.findAll();
        assertEquals(initCount+1, orders.size());

        // Delete using controller.
        controller.deleteOne(newOrder.getId());
        orders = (List<Order>) repository.findAll();
        assertEquals(initCount, orders.size());

        // Clean Up.
        repository.delete(newOrder);
    }
}