package vubq.warehouse_management.VT_EcoStorage.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vubq.warehouse_management.VT_EcoStorage.dtos.ProductCategoryDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.ProductOriginDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.ProductUnitDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReferenceDataProductResponse {

    private List<ProductCategoryDto> productCategories;
    private List<ProductUnitDto> productUnits;
    private List<ProductOriginDto> productOrigins;
}
