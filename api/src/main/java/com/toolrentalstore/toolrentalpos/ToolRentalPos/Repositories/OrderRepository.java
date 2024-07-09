package com.toolrentalstore.toolrentalpos.ToolRentalPos.Repositories;

import com.toolrentalstore.toolrentalpos.ToolRentalPos.Models.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends CrudRepository<Order, Integer> {
}
