package com.teamviewer.remotedesktopshop.RemoteDesktopShop.Controllers;

import com.teamviewer.remotedesktopshop.RemoteDesktopShop.Models.Product;
import com.teamviewer.remotedesktopshop.RemoteDesktopShop.Repositories.ProductRepository;
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
        if (productList.size() == 0) {
            productList = new ArrayList<>();
            productList.add(new Product(null, "Windows Machine", 24.5f, "This is windows 11 based remote desktop.", "https://i.pcmag.com/imagery/reviews/00xBy0JjVybodfIwWxeGCkZ-1..v1679417407.jpg"));
            productList.add(new Product(null, "Mac OS Machine", 30.5f, "This is mac os based remote desktop.", "https://images.unsplash.com/photo-1514826786317-59744fe2a548?q=80&w=1000&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTR8fGFwcGxlJTIwbWFjfGVufDB8fDB8fHww"));
            productList.add(new Product(null, "Ubuntu Machine", 15.5f, "This is ubuntu based remote desktop.", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSd6yvCeIVbHXOcwzN9CzDKNKwykpZ_lYDR7PewI66L7upLMaALB9OUX45FTdprea-xlTc&usqp=CAU"));
        }


        repository.saveAll(productList);
    }
}
