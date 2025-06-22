package vubq.warehouse_management.VT_EcoStorage.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vubq.warehouse_management.VT_EcoStorage.entities.ProductUnit;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductUnitDto {
    private String id;
    private String name;
    private ProductUnit.Status status;

    public static ProductUnitDto toDto(ProductUnit productUnit) {
        return ProductUnitDto.builder()
                .id(productUnit.getId())
                .name(productUnit.getName())
                .status(productUnit.getStatus())
                .build();
    }
}
