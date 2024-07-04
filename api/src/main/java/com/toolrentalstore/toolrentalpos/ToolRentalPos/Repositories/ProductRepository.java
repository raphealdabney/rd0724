package com.teamviewer.remotedesktopshop.RemoteDesktopShop.Repositories;


import com.teamviewer.remotedesktopshop.RemoteDesktopShop.Models.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<Product, Integer> {
}
