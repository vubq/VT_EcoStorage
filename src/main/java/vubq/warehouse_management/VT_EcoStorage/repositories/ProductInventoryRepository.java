package vubq.warehouse_management.VT_EcoStorage.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vubq.warehouse_management.VT_EcoStorage.entities.ProductInventory;

@Repository
public interface ProductInventoryRepository extends JpaRepository<ProductInventory, String> {
    Page<ProductInventory> findByProductId(String productId, Pageable pageable);
}
