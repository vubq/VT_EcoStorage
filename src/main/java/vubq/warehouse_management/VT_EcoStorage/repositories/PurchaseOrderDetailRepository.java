package vubq.warehouse_management.VT_EcoStorage.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vubq.warehouse_management.VT_EcoStorage.entities.PurchaseOrderDetail;

import java.util.List;

@Repository
public interface PurchaseOrderDetailRepository extends JpaRepository<PurchaseOrderDetail, String> {
    void deleteByIdIn(List<String> ids);

    List<PurchaseOrderDetail> findByPurchaseOrderId(String purchaseOrderId);
}
