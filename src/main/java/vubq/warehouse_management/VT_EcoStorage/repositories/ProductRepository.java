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

    @Query(value = """
            SELECT 
                wh.id AS warehouse_id,
                wh.name AS warehouse_name,
                p.id AS product_id,
                p.name AS product_name,
                p.barcode AS product_barcode,
                p.sku AS product_sku,

                COALESCE(SUM(CASE 
                    WHEN po.status = 'RECEIVED' 
                     AND po.received_date BETWEEN :startDate AND :endDate 
                    THEN pod.quantity ELSE 0 END), 0) AS total_import_quantity,

                COALESCE(SUM(CASE 
                    WHEN po.status = 'RECEIVED' 
                     AND po.received_date BETWEEN :startDate AND :endDate 
                    THEN pod.total_amount ELSE 0 END), 0) AS total_import_amount,

                COALESCE(SUM(CASE 
                    WHEN eo.status = 'DELIVERED' 
                     AND eo.delivered_date BETWEEN :startDate AND :endDate 
                    THEN eod.quantity ELSE 0 END), 0) AS total_export_quantity,

                COALESCE(SUM(CASE 
                    WHEN eo.status = 'DELIVERED' 
                     AND eo.delivered_date BETWEEN :startDate AND :endDate 
                    THEN eod.total_amount ELSE 0 END), 0) AS total_export_amount

            FROM tb_products p

            LEFT JOIN tb_purchase_order_details pod ON pod.product_id = p.id
            LEFT JOIN tb_purchase_orders po ON pod.purchase_order_id = po.id

            LEFT JOIN tb_export_order_details eod ON eod.product_id = p.id
            LEFT JOIN tb_export_orders eo ON eod.export_order_id = eo.id

            LEFT JOIN tb_warehouses wh ON wh.id = COALESCE(po.warehouse_id, eo.warehouse_id)

            GROUP BY wh.id, wh.name, p.id, p.name, p.barcode, p.sku
            ORDER BY wh.name, p.name
            """, nativeQuery = true)
    List<Object[]> getWarehouseProductStats(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
