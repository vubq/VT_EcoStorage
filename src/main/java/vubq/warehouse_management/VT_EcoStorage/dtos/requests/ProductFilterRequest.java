package vubq.warehouse_management.VT_EcoStorage.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductFilterRequest {
    private String productCategoryId;
    private String productOriginId;
    private String productUnitId;
}
