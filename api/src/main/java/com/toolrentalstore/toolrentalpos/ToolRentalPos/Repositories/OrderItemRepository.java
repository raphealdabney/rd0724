package com.teamviewer.remotedesktopshop.RemoteDesktopShop.Repositories;

import com.teamviewer.remotedesktopshop.RemoteDesktopShop.Models.OrderItem;
import org.springframework.data.repository.CrudRepository;

public interface OrderItemRepository extends CrudRepository<OrderItem, Integer> {
}
