package vubq.warehouse_management.VT_EcoStorage.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vubq.warehouse_management.VT_EcoStorage.entities.PurchaseOrder;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, String> {
    void deleteByIdIn(Collection<String> ids);

    Page<PurchaseOrder> findAll(Specification<PurchaseOrder> spec, Pageable pageable);

    Optional<PurchaseOrder> findByExportOrderId(String exportOrderId);
}
