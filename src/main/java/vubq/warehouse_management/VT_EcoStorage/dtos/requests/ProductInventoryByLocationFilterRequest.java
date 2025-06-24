package vubq.warehouse_management.VT_EcoStorage.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductInventoryByLocationFilterRequest {

    private String productCategoryId;
    private String zoneId;
    private String shelfId;
    private String floorId;
}
