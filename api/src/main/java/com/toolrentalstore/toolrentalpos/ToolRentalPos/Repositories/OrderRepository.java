package com.teamviewer.remotedesktopshop.RemoteDesktopShop.Repositories;

import com.teamviewer.remotedesktopshop.RemoteDesktopShop.Models.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends CrudRepository<Order, Integer> {
}
