package vubq.warehouse_management.VT_EcoStorage.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vubq.warehouse_management.VT_EcoStorage.dtos.ProductStatistics;
import vubq.warehouse_management.VT_EcoStorage.entities.ExportOrder;
import vubq.warehouse_management.VT_EcoStorage.entities.Product;
import vubq.warehouse_management.VT_EcoStorage.entities.PurchaseOrder;

import java.util.Date;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    Page<Product> findAll(Specification<Product> spec, Pageable pageable);

    List<Product> findByIdIn(List<String> ids);

    @Query("""
                SELECT new vubq.warehouse_management.VT_EcoStorage.dtos.ProductStatistics(
                    pod.productId,
                    SUM(pod.quantity),
                    SUM(pod.totalAmount)
                )
                FROM PurchaseOrderDetail pod
                JOIN pod.purchaseOrder po
                WHERE po.status = :statusReceived
                  AND po.receivedDate BETWEEN :fromDate AND :toDate
                GROUP BY pod.productId
            """)
    List<ProductStatistics> findReceivedStats(
            @Param("statusReceived") PurchaseOrder.Status statusReceived,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );

    @Query("""
                SELECT new vubq.warehouse_management.VT_EcoStorage.dtos.ProductStatistics(
                    eod.productId,
                    SUM(eod.quantity),
                    SUM(eod.totalAmount)
                )
                FROM ExportOrderDetail eod
                JOIN eod.exportOrder eo
                WHERE eo.status = :statusDelivered
                  AND eo.deliveredDate BETWEEN :fromDate AND :toDate
                GROUP BY eod.productId
            """)
    List<ProductStatistics> findDeliveredStats(
            @Param("statusDelivered") ExportOrder.Status statusDelivered,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );

}
