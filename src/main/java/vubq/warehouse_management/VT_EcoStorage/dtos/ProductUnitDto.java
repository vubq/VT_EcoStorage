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
    private String description;
    private String note;
    private ProductUnit.Status status;

    public static ProductUnitDto toDto(ProductUnit productUnit) {
        return ProductUnitDto.builder()
                .id(productUnit.getId())
                .name(productUnit.getName())
                .description(productUnit.getDescription())
                .note(productUnit.getNote())
                .status(productUnit.getStatus())
                .build();
    }
}
