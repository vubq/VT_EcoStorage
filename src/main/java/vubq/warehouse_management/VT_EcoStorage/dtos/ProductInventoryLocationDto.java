package vubq.warehouse_management.VT_EcoStorage.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vubq.warehouse_management.VT_EcoStorage.entities.ProductInventoryLocation;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductInventoryLocationDto {
    private String id;
    private ProductInventoryLocation.Status status;
    private String locationId;
    private String purchaseOrderDetailId;
}
