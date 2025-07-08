package vubq.warehouse_management.VT_EcoStorage.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vubq.warehouse_management.VT_EcoStorage.entities.views.ProductByLocation;

import java.util.List;

@Repository
public interface ProductByLocationRepository extends JpaRepository<ProductByLocation, String> {

    Page<ProductByLocation> findAll(Specification<ProductByLocation> spec, Pageable pageable);

    List<ProductByLocation> findByFloorId(String floorId);

    List<ProductByLocation> findByFloorIdAndInventoryQuantityGreaterThan(String floorId, Long quantity);
}
