package com.toolrentalstore.toolrentalpos.ToolRentalPos.Services;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.toolrentalstore.toolrentalpos.ToolRentalPos.Models.Cart;
import com.toolrentalstore.toolrentalpos.ToolRentalPos.Models.CartItem;
import com.toolrentalstore.toolrentalpos.ToolRentalPos.Models.Product;
import com.toolrentalstore.toolrentalpos.ToolRentalPos.Repositories.ProductRepository;

@Service
public class OrderService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    public OrderService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Float calculateTotalForCart(Cart cart) {
        float rentalFees = calculateRentalFeesForCart(cart);
        float discountAmount = rentalFees * cart.getDiscountPercent();
        return rentalFees - discountAmount;
    }

    public float calculateDiscountAmountForCart(Cart cart) {
        float rentalFees = calculateRentalFeesForCart(cart);
        float discountAmount = rentalFees * cart.getDiscountPercent();
        return Math.round(discountAmount * 100) / 100;
    }

    public Float calculateRentalFeesForCart(Cart cart) {
        int dailyChargesAggregate = 0;

        // Get Products & Days Individually.
       for (CartItem item : cart.getItems()) {
           // Get chargable days for cart.
            Product curProduct = findProductByCode(item.getToolCode());
            int chargableDaysForProduct = getChargableDaysForProductForDaysStarting(product, item.getRentalDays(), cart.getCheckoutDate());

       };

        return 0.01f;
    }

    public int getChargableDaysForProductForDaysStarting(Product product, String tool_code, int daysToRent) {
            boolean productChargableWeekDay =  product.getCharges_weekday() == 1 ? true : false;
            boolean productChargableWeekEnd =  product.getCharges_weekend() == 1 ? true : false;
            boolean productChargableHoliday =  product.getCharges_holiday() == 1 ? true : false;
            double productCostPerDay = product.getCharges_daily();
        return 0;
    }

    public Product findProductByCode(String tool_code) {
        Product searcher = new Product();
        searcher.setTool_code(tool_code);
        Example<Product> productExample = Example.of(searcher);
        Optional<Product> result = productRepository.findOne(productExample);
        return result.isEmpty() ? null : result.get();
    }
}
