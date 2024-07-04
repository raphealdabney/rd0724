package com.teamviewer.remotedesktopshop.RemoteDesktopShop.Controllers;

import com.teamviewer.remotedesktopshop.RemoteDesktopShop.Models.Order;
import com.teamviewer.remotedesktopshop.RemoteDesktopShop.Models.Product;
import com.teamviewer.remotedesktopshop.RemoteDesktopShop.Repositories.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;


@RestController
public class ShopOrderController implements BaseController {

    private final OrderRepository repository;

    public ShopOrderController(OrderRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/orders")
    public Iterable<Order> findAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/orders/{id}")
    @ResponseBody
    public Order findOne(@PathVariable Integer id) {
        Optional<Order> order = repository.findById(id);
        if (order.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        } else {
            return order.get();
        }
    }

    @PostMapping(value = "/orders")
    @ResponseBody
    public Order createOne(@RequestBody Order data) {
        try {
            return repository.save(data);
        } catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Resource not created");
        }
    }

    @PutMapping(value = "/orders/{id}")
    @ResponseBody
    public Order updateOne(@PathVariable Integer id, @RequestBody Order data) {
        Optional<Order> order = repository.findById(id);
        if (order.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        } else {
            Order currentOrder = order.get();
            data.setId(currentOrder.getId());
            return repository.save(data);
        }
    }

    @DeleteMapping(value = "/orders/{id}")
    @ResponseBody
    public Boolean deleteOne(@PathVariable Integer id) {
        Optional<Order> order = repository.findById(id);
        if (order.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        } else {
            repository.delete(order.get());
        }
        return null;
    }

}
