package vubq.warehouse_management.VT_EcoStorage.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vubq.warehouse_management.VT_EcoStorage.entities.ProductInventoryLocation;

import java.util.List;

@Repository
public interface ProductInventoryLocationRepository extends JpaRepository<ProductInventoryLocation, String> {

    List<ProductInventoryLocation> findByProductIdInAndLocationIdIn(
            List<String> productIds,
            List<String> locationIds
    );

    List<ProductInventoryLocation> findByIdIn(List<String> ids);
}
