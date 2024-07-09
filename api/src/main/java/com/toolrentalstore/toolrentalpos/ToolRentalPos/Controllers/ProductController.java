package com.toolrentalstore.toolrentalpos.ToolRentalPos.Controllers;

import com.toolrentalstore.toolrentalpos.ToolRentalPos.Models.Product;
import com.toolrentalstore.toolrentalpos.ToolRentalPos.Repositories.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
public class ProductController implements BaseController {

    private final ProductRepository repository;

    public ProductController(ProductRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/products")
    @ResponseBody
    public Iterable<Product> findAll() {
        return repository.findAll();
    }

    @GetMapping(value = "/products/{id}")
    @ResponseBody
    public Product findOne(@PathVariable Integer id) {
        Optional<Product> prod = repository.findById(id);
        if (prod.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        } else {
            return prod.get();
        }
    }

    @PostMapping(value = "/products")
    @ResponseBody
    public Product createOne(@RequestBody Product data) {
        try {
            return repository.save(data);
        } catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Resource not created");
        }
    }

    @PutMapping(value = "/products/{id}")
    @ResponseBody
    public Product updateOne(@PathVariable Integer id, @RequestBody Product data) {
        Optional<Product> prod = repository.findById(id);
        if (prod.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        } else {
            Product currentProduct = prod.get();
            data.setId(currentProduct.getId());
            return repository.save(data);
        }
    }

    @DeleteMapping(value = "/products/{id}")
    @ResponseBody
    public Boolean deleteOne(@PathVariable Integer id) {
        Optional<Product> prod = repository.findById(id);
        if (prod.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        } else {
            repository.delete(prod.get());
        }
        return null;
    }

    @GetMapping(value = "/products/populate")
    public void populate() {
        List<Product> productList = (List<Product>) repository.findAll();
        
    }
}
