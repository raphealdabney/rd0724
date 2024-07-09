package com.toolrentalstore.toolrentalpos.ToolRentalPos.Repositories;

import com.toolrentalstore.toolrentalpos.ToolRentalPos.Models.OrderItem;
import org.springframework.data.repository.CrudRepository;

public interface OrderItemRepository extends CrudRepository<OrderItem, Integer> {
}
