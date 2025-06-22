package vubq.warehouse_management.VT_EcoStorage.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vubq.warehouse_management.VT_EcoStorage.entities.ProductCategory;

import java.util.List;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, String> {

    List<ProductCategory> findByStatus(ProductCategory.Status status);

    Page<ProductCategory> findAll(Specification<ProductCategory> spec, Pageable pageable);
}
