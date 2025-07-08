package vubq.warehouse_management.VT_EcoStorage.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vubq.warehouse_management.VT_EcoStorage.dtos.ProductCategoryDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.WarehouseDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReferenceDataInventoryResponse {
    List<WarehouseDto> warehouses;
    List<ProductCategoryDto> categories;
}
