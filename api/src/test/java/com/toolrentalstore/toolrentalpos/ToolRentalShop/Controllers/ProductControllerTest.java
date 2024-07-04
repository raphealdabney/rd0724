package com.teamviewer.remotedesktopshop.RemoteDesktopShop.Controllers;

import com.teamviewer.remotedesktopshop.RemoteDesktopShop.Models.Product;
import com.teamviewer.remotedesktopshop.RemoteDesktopShop.Models.Product;
import com.teamviewer.remotedesktopshop.RemoteDesktopShop.Repositories.OrderRepository;
import com.teamviewer.remotedesktopshop.RemoteDesktopShop.Repositories.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductControllerTest {

    @Autowired
    private ProductController controller;
    @Autowired
    private ProductRepository repository;

    @AfterEach
    void tearDown() {
    }

    @Test
    void findAll() {
        // Find all and take count.
        List<Product> products = (List<Product>) controller.findAll();
        int initialCount = products.size();

        // Create Shop Product.
        Product product1 = new Product();
        repository.save(product1);

        // Create shop product.
        Product product2 = new Product();
        repository.save(product2);

        // findall and see that count delta is == 2
        products = (List<Product>) controller.findAll();
        int afterCount = products.size();
        assertEquals(2, afterCount - initialCount);

        // clean up
        repository.delete(product1);
        repository.delete(product2);

    }

    @Test
    void findOne() {
        // Create Shop Product.
        Product product1 = new Product();
        repository.save(product1);

        // Create shop product.
        Product product2 = new Product();
        repository.save(product2);

        // Find one using controller.
        Product product = controller.findOne(product2.getId());
        assertNotNull(product);

        // Clean up.
        repository.delete(product1);
        repository.delete(product2);
    }

    @Test
    void createOne() {
        Product product1 = new Product();
        product1.setName("whoa");
        List<Product> products = (List<Product>) repository.findAll();
        int initCount = products.size();

        Product newOrder = controller.createOne(product1);
        products = (List<Product>) repository.findAll();
        assertEquals(1, products.size() - initCount);

        // Clean Up.
        repository.delete(newOrder);
    }

    @Test
    void updateOne() {
        // Create Product.
        Product product1 = new Product();
        product1.setName("whatwhat");
        Product savedOrder = repository.save(product1);

        // Update Product.
        Product newOrderData = new Product();
        newOrderData.setName("changedd");
        Product newSavedOrder = controller.updateOne(savedOrder.getId(), newOrderData);
        assertEquals("changedd", newSavedOrder.getName());

        // Clean up.
        repository.delete(newSavedOrder);
    }

    @Test
    void deleteOne() {
        // Create product
        Product product1 = new Product();
        product1.setName("whoa");

        // Get count before adding.
        List<Product> products = (List<Product>) repository.findAll();
        int initCount = products.size();

        // Add product then assert it went up by one.
        Product newOrder = repository.save(product1);
        products = (List<Product>) repository.findAll();
        assertEquals(initCount+1, products.size());

        // Delete using controller.
        controller.deleteOne(newOrder.getId());
        products = (List<Product>) repository.findAll();
        assertEquals(initCount, products.size());

        // Clean Up.
        repository.delete(newOrder);
    }
}