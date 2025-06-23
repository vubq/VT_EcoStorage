package vubq.warehouse_management.VT_EcoStorage.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vubq.warehouse_management.VT_EcoStorage.entities.ExportOrderDetail;

import java.util.List;

@Repository
public interface ExportOrderDetailRepository extends JpaRepository<ExportOrderDetail, String> {
    void deleteByIdIn(List<String> ids);

    List<ExportOrderDetail> findByExportOrderId(String exportOrderId);
}
