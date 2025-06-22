package vubq.warehouse_management.VT_EcoStorage.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vubq.warehouse_management.VT_EcoStorage.entities.ProductCategory;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCategoryDto {
    private String id;
    private String name;
    private ProductCategory.Status status;

    public static ProductCategoryDto toDto(ProductCategory productCategory) {
        return ProductCategoryDto.builder()
                .id(productCategory.getId())
                .name(productCategory.getName())
                .status(productCategory.getStatus())
                .build();
    }
}
