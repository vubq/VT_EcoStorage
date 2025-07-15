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

    boolean existsByNameAndProductCategoryId(String name, String productCategoryId);

    boolean existsByNameAndProductCategoryIdAndIdNot(String name, String productCategoryId, String id);

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
                WITH all_product_warehouses AS (
                    SELECT 
                        p.id AS product_id,
                        w.id AS warehouse_id,
                        p.name AS product_name,
                        p.barcode AS product_barcode,
                        p.sku AS product_sku,
                        w.name AS warehouse_name
                    FROM tb_products p
                    CROSS JOIN tb_warehouses w
                ),
                import_data AS (
                    SELECT
                        pod.product_id,
                        po.warehouse_id,
                        SUM(pod.quantity) AS total_import_quantity,
                        SUM(pod.total_amount) AS total_import_amount
                    FROM tb_purchase_order_details pod
                    JOIN tb_purchase_orders po ON pod.purchase_order_id = po.id
                    WHERE po.status = 'RECEIVED'
                      AND po.received_date BETWEEN :startDate AND :endDate
                    GROUP BY pod.product_id, po.warehouse_id
                ),
                export_data AS (
                    SELECT
                        eod.product_id,
                        eo.warehouse_id,
                        SUM(eod.quantity) AS total_export_quantity,
                        SUM(eod.total_amount) AS total_export_amount
                    FROM tb_export_order_details eod
                    JOIN tb_export_orders eo ON eod.export_order_id = eo.id
                    WHERE eo.status = 'DELIVERED'
                      AND eo.delivered_date BETWEEN :startDate AND :endDate
                    GROUP BY eod.product_id, eo.warehouse_id
                )

                SELECT 
                    apw.warehouse_id,
                    apw.warehouse_name,
                    apw.product_id,
                    apw.product_name,
                    apw.product_barcode,
                    apw.product_sku,
                    COALESCE(i.total_import_quantity, 0) AS total_import_quantity,
                    COALESCE(i.total_import_amount, 0) AS total_import_amount,
                    COALESCE(e.total_export_quantity, 0) AS total_export_quantity,
                    COALESCE(e.total_export_amount, 0) AS total_export_amount
                FROM all_product_warehouses apw
                LEFT JOIN import_data i ON apw.product_id = i.product_id AND apw.warehouse_id = i.warehouse_id
                LEFT JOIN export_data e ON apw.product_id = e.product_id AND apw.warehouse_id = e.warehouse_id
                ORDER BY apw.warehouse_name, apw.product_name
            """, nativeQuery = true)
    List<Object[]> getWarehouseProductStats(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = """
                WITH all_product_warehouses AS (
                    SELECT 
                        p.id AS product_id,
                        w.id AS warehouse_id,
                        p.name AS product_name,
                        p.barcode AS product_barcode,
                        p.sku AS product_sku,
                        w.name AS warehouse_name
                    FROM tb_products p
                    CROSS JOIN tb_warehouses w
                ),
                import_data AS (
                    SELECT
                        pod.product_id,
                        po.warehouse_id,
                        SUM(pod.quantity) AS total_import_quantity,
                        SUM(pod.total_amount) AS total_import_amount
                    FROM tb_purchase_order_details pod
                    JOIN tb_purchase_orders po ON pod.purchase_order_id = po.id
                    WHERE po.status = 'RECEIVED'
                      AND po.received_date BETWEEN :startDate AND :endDate
                    GROUP BY pod.product_id, po.warehouse_id
                ),
                export_data AS (
                    SELECT
                        eod.product_id,
                        eo.warehouse_id,
                        SUM(eod.quantity) AS total_export_quantity,
                        SUM(eod.total_amount) AS total_export_amount
                    FROM tb_export_order_details eod
                    JOIN tb_export_orders eo ON eod.export_order_id = eo.id
                    WHERE eo.status = 'DELIVERED'
                      AND eo.delivered_date BETWEEN :startDate AND :endDate
                    GROUP BY eod.product_id, eo.warehouse_id
                )

                SELECT 
                    apw.warehouse_id,
                    apw.warehouse_name,
                    apw.product_id,
                    apw.product_name,
                    apw.product_barcode,
                    apw.product_sku,
                    COALESCE(i.total_import_quantity, 0) AS total_import_quantity,
                    COALESCE(i.total_import_amount, 0) AS total_import_amount,
                    COALESCE(e.total_export_quantity, 0) AS total_export_quantity,
                    COALESCE(e.total_export_amount, 0) AS total_export_amount
                FROM all_product_warehouses apw
                LEFT JOIN import_data i ON apw.product_id = i.product_id AND apw.warehouse_id = i.warehouse_id
                LEFT JOIN export_data e ON apw.product_id = e.product_id AND apw.warehouse_id = e.warehouse_id
                WHERE (:onlyWithTransaction = false 
                       OR COALESCE(i.total_import_quantity, 0) > 0 
                       OR COALESCE(e.total_export_quantity, 0) > 0)
                ORDER BY apw.warehouse_name, apw.product_name
            """, nativeQuery = true)
    List<Object[]> getWarehouseProductStats(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("onlyWithTransaction") boolean onlyWithTransaction
    );

    @Query(value = """
                WITH all_product_warehouses AS (
                    SELECT 
                        p.id AS product_id,
                        w.id AS warehouse_id,
                        p.name AS product_name,
                        p.barcode AS product_barcode,
                        p.sku AS product_sku,
                        w.name AS warehouse_name
                    FROM tb_products p
                    CROSS JOIN tb_warehouses w
                    WHERE 
                        (:warehouseId = 'ALL' OR w.id = :warehouseId)
                        AND (
                            :keyword IS NULL OR 
                            LOWER(p.name) ILIKE LOWER(CONCAT('%', :keyword, '%')) OR 
                            LOWER(p.barcode) ILIKE LOWER(CONCAT('%', :keyword, '%')) OR 
                            LOWER(p.sku) ILIKE LOWER(CONCAT('%', :keyword, '%'))
                        )
                ),
                import_data AS (
                    SELECT
                        pod.product_id,
                        po.warehouse_id,
                        SUM(pod.quantity) AS total_import_quantity,
                        SUM(pod.total_amount) AS total_import_amount
                    FROM tb_purchase_order_details pod
                    JOIN tb_purchase_orders po ON pod.purchase_order_id = po.id
                    WHERE po.status = 'RECEIVED'
                      AND po.received_date BETWEEN :startDate AND :endDate
                    GROUP BY pod.product_id, po.warehouse_id
                ),
                export_data AS (
                    SELECT
                        eod.product_id,
                        eo.warehouse_id,
                        SUM(eod.quantity) AS total_export_quantity,
                        SUM(eod.total_amount) AS total_export_amount
                    FROM tb_export_order_details eod
                    JOIN tb_export_orders eo ON eod.export_order_id = eo.id
                    WHERE eo.status = 'DELIVERED'
                      AND eo.delivered_date BETWEEN :startDate AND :endDate
                    GROUP BY eod.product_id, eo.warehouse_id
                )

                SELECT 
                    apw.warehouse_id,
                    apw.warehouse_name,
                    apw.product_id,
                    apw.product_name,
                    apw.product_barcode,
                    apw.product_sku,
                    COALESCE(i.total_import_quantity, 0) AS total_import_quantity,
                    COALESCE(i.total_import_amount, 0) AS total_import_amount,
                    COALESCE(e.total_export_quantity, 0) AS total_export_quantity,
                    COALESCE(e.total_export_amount, 0) AS total_export_amount
                FROM all_product_warehouses apw
                LEFT JOIN import_data i ON apw.product_id = i.product_id AND apw.warehouse_id = i.warehouse_id
                LEFT JOIN export_data e ON apw.product_id = e.product_id AND apw.warehouse_id = e.warehouse_id
                WHERE (:onlyWithTransaction = false 
                       OR COALESCE(i.total_import_quantity, 0) > 0 
                       OR COALESCE(e.total_export_quantity, 0) > 0)
                ORDER BY apw.warehouse_name, apw.product_name
            """, nativeQuery = true)
    List<Object[]> getWarehouseProductStats(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("warehouseId") String warehouseId,
            @Param("keyword") String keyword,
            @Param("onlyWithTransaction") boolean onlyWithTransaction
    );

}
