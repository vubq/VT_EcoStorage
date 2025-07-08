package vubq.warehouse_management.VT_EcoStorage.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vubq.warehouse_management.VT_EcoStorage.dtos.InventoryLocationDto;
import vubq.warehouse_management.VT_EcoStorage.entities.ProductInventoryLocation;

import java.util.List;

@Repository
public interface ProductInventoryLocationRepository extends JpaRepository<ProductInventoryLocation, String> {

    List<ProductInventoryLocation> findByProductIdInAndLocationIdIn(
            List<String> productIds,
            List<String> locationIds
    );

    List<ProductInventoryLocation> findByIdIn(List<String> ids);

//    @Query("""
//            SELECT new vubq.warehouse_management.VT_EcoStorage.dtos.InventoryLocationDto(
//                w.id, w.name,
//                z.id, z.name,
//                s.id, s.name,
//                f.id, f.floor,
//                p.id, p.name, p.barcode, p.sku,
//                pil.inventoryQuantity
//            )
//            FROM ProductInventoryLocation pil
//            JOIN pil.floor f
//            JOIN f.shelf s
//            JOIN s.zone z
//            JOIN z.warehouse w
//            LEFT JOIN pil.product p
//            WHERE pil.inventoryQuantity IS NOT NULL AND pil.inventoryQuantity > 0
//            ORDER BY w.name, z.name, s.name, f.floor, p.name
//            """)
//    List<InventoryLocationDto> findAllInventoryWithLocation();

    @Query("""
                SELECT new vubq.warehouse_management.VT_EcoStorage.dtos.InventoryLocationDto(
                    w.id, w.name,
                    z.id, z.name,
                    s.id, s.name,
                    f.id, f.floor,
                    p.id, p.name, p.barcode, p.sku,
                    pc.id, pc.name,
                    pu.id, pu.name,
                    pil.inventoryQuantity
                )
                FROM ProductInventoryLocation pil
                JOIN pil.floor f
                JOIN f.shelf s
                JOIN s.zone z
                JOIN z.warehouse w
                LEFT JOIN pil.product p
                LEFT JOIN p.productCategory pc
                LEFT JOIN p.productUnit pu
                WHERE pil.inventoryQuantity IS NOT NULL AND pil.inventoryQuantity > 0
                  AND (:warehouseId = 'ALL' OR w.id = :warehouseId)
                  AND (:productCategoryId = 'ALL' OR pc.id = :productCategoryId)
                  AND (
                      :keyword IS NULL OR :keyword = '' OR
                      LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                      LOWER(p.barcode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                      LOWER(p.sku) LIKE LOWER(CONCAT('%', :keyword, '%'))
                  )
                ORDER BY w.name, z.name, s.name, f.floor, p.name
            """)
    List<InventoryLocationDto> findAllInventoryWithLocation(
            @Param("warehouseId") String warehouseId,
            @Param("productCategoryId") String productCategoryId,
            @Param("keyword") String keyword
    );

}
