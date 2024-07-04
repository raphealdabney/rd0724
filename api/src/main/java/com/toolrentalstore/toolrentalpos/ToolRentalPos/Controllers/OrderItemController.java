package com.teamviewer.remotedesktopshop.RemoteDesktopShop.Controllers;

import com.teamviewer.remotedesktopshop.RemoteDesktopShop.Models.OrderItem;
import com.teamviewer.remotedesktopshop.RemoteDesktopShop.Models.Product;
import com.teamviewer.remotedesktopshop.RemoteDesktopShop.Repositories.OrderItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
public class OrderItemController implements BaseController {

    final private OrderItemRepository repository;

    public OrderItemController(OrderItemRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/order-items")
    public Iterable<OrderItem> findAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/order-items/{id}")
    @ResponseBody
    public OrderItem findOne(@PathVariable Integer id) {
        Optional<OrderItem> order = repository.findById(id);
        if (order.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        } else {
            return order.get();
        }
    }

    @PostMapping(value = "/order-items")
    @ResponseBody
    public OrderItem createOne(@RequestBody OrderItem data) {
        try {
            return repository.save(data);
        } catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Resource not created");
        }
    }

    @PutMapping(value = "/order-items/{id}")
    @ResponseBody
    public OrderItem updateOne(@PathVariable Integer id, @RequestBody OrderItem data) {
        Optional<OrderItem> orderItem = repository.findById(id);
        if (orderItem.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        } else {
            OrderItem currentItem = orderItem.get();
            data.setId(currentItem.getId());
            return repository.save(data);
        }
    }

    @DeleteMapping(value = "/order-items/{id}")
    @ResponseBody
    public Boolean deleteOne(@PathVariable Integer id) {
        Optional<OrderItem> orderItem = repository.findById(id);
        if (orderItem.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        } else {
            repository.delete(orderItem.get());
        }
        return null;
    }



}
