package vubq.warehouse_management.VT_EcoStorage.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vubq.warehouse_management.VT_EcoStorage.entities.ProductOrigin;

import java.util.List;

@Repository
public interface ProductOriginRepository extends JpaRepository<ProductOrigin, String> {

    List<ProductOrigin> findByStatus(ProductOrigin.Status status);

    Page<ProductOrigin> findAll(Specification<ProductOrigin> spec, Pageable pageable);
}
