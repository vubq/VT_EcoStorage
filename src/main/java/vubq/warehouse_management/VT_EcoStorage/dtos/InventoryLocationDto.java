package vubq.warehouse_management.VT_EcoStorage.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryLocationDto {

    private String warehouseId;
    private String warehouseName;
    private String zoneId;
    private String zoneName;
    private String shelfId;
    private String shelfName;
    private String floorId;
    private Long floorNumber;
    private String productId;
    private String productName;
    private String productBarcode;
    private String productSKU;
    private Long inventoryQuantity;
}
