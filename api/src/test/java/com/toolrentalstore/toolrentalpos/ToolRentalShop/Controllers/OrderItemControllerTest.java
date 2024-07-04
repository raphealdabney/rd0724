package com.teamviewer.remotedesktopshop.RemoteDesktopShop.Controllers;

import com.teamviewer.remotedesktopshop.RemoteDesktopShop.Models.OrderItem;
import com.teamviewer.remotedesktopshop.RemoteDesktopShop.Repositories.OrderItemRepository;
import com.teamviewer.remotedesktopshop.RemoteDesktopShop.Repositories.OrderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderItemControllerTest {

    @Autowired
    private OrderItemController controller;
    @Autowired
    private OrderItemRepository repository;

    @AfterEach
    void tearDown() {
    }

    @Test
    void findAll() {
        // Find all and take count.
        List<OrderItem> orderItems = (List<OrderItem>) controller.findAll();
        int initialCount = orderItems.size();

        // Create Shop OrderItem.
        OrderItem order1 = new OrderItem();
        repository.save(order1);

        // Create shop orderItem.
        OrderItem order2 = new OrderItem();
        repository.save(order2);

        // findall and see that count delta is == 2
        orderItems = (List<OrderItem>) controller.findAll();
        int afterCount = orderItems.size();
        assertEquals(2, afterCount - initialCount);

        // clean up
        repository.delete(order1);
        repository.delete(order2);

    }

    @Test
    void findOne() {
        // Create Shop OrderItem.
        OrderItem order1 = new OrderItem();
        repository.save(order1);

        // Create shop orderItem.
        OrderItem order2 = new OrderItem();
        repository.save(order2);

        // Find one using controller.
        OrderItem orderItem = controller.findOne(order2.getId());
        assertNotNull(orderItem);

        // Clean up.
        repository.delete(order1);
        repository.delete(order2);
    }

    @Test
    void createOne() {
        OrderItem order1 = new OrderItem();
        List<OrderItem> orderItems = (List<OrderItem>) repository.findAll();
        int initCount = orderItems.size();

        OrderItem newOrder = controller.createOne(order1);
        orderItems = (List<OrderItem>) repository.findAll();
        assertEquals(1, orderItems.size() - initCount);

        // Clean Up.
        repository.delete(newOrder);
    }

    @Test
    void updateOne() {
        // Create OrderItem.
        OrderItem order1 = new OrderItem();
        order1.setImageUrl("whatwhat");
        OrderItem savedOrder = repository.save(order1);

        // Update OrderItem.
        OrderItem newOrderData = new OrderItem();
        newOrderData.setImageUrl("changedd");
        OrderItem newSavedOrder = controller.updateOne(savedOrder.getId(), newOrderData);
        assertEquals("changedd", newSavedOrder.getImageUrl());

        // Clean up.
        repository.delete(newSavedOrder);
    }

    @Test
    void deleteOne() {
        // Create orderItem
        OrderItem order1 = new OrderItem();
        order1.setImageUrl("whoa");

        // Get count before adding.
        List<OrderItem> orderItems = (List<OrderItem>) repository.findAll();
        int initCount = orderItems.size();

        // Add orderItem then assert it went up by one.
        OrderItem newOrder = repository.save(order1);
        orderItems = (List<OrderItem>) repository.findAll();
        assertEquals(initCount+1, orderItems.size());

        // Delete using controller.
        controller.deleteOne(newOrder.getId());
        orderItems = (List<OrderItem>) repository.findAll();
        assertEquals(initCount, orderItems.size());

        // Clean Up.
        repository.delete(newOrder);
    }
}