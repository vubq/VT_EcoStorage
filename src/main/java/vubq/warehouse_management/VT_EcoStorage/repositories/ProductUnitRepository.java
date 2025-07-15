package vubq.warehouse_management.VT_EcoStorage.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vubq.warehouse_management.VT_EcoStorage.entities.ProductUnit;

import java.util.List;

@Repository
public interface ProductUnitRepository extends JpaRepository<ProductUnit, String> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, String id);

    List<ProductUnit> findByStatus(ProductUnit.Status status);

    Page<ProductUnit> findAll(Specification<ProductUnit> spec, Pageable pageable);
}
