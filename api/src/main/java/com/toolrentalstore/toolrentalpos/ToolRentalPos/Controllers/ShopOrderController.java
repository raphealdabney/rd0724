package com.toolrentalstore.toolrentalpos.ToolRentalPos.Controllers;

import com.toolrentalstore.toolrentalpos.ToolRentalPos.Models.Cart;
import com.toolrentalstore.toolrentalpos.ToolRentalPos.Models.Order;
import com.toolrentalstore.toolrentalpos.ToolRentalPos.Repositories.OrderRepository;
import com.toolrentalstore.toolrentalpos.ToolRentalPos.Services.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@CrossOrigin
@RestController
public class ShopOrderController implements BaseController {

    private final OrderRepository repository;
    @Autowired
    private final OrderService orderService;

    public ShopOrderController(OrderRepository repository, OrderService orderService) {
        this.repository = repository;
        this.orderService = orderService;
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
    public Map<String, String> createOne(@RequestBody Cart cart) {
        try {
            Map<String, String> response = new HashMap<String,String>();
            response.put("html","<h1>Data returned</h1>");
            return response;
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

    @PostMapping(value = "/orders/calc-cart-price") 
    @ResponseBody
    public double calculateCartTotal(@RequestBody Cart cart ) {   

        return orderService.calculateTotalForCart(cart);
    }

}
