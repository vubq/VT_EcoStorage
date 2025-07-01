package vubq.warehouse_management.VT_EcoStorage.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vubq.warehouse_management.VT_EcoStorage.entities.ProductOrigin;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductOriginDto {
    private String id;
    private String name;
    private String description;
    private String note;
    private ProductOrigin.Status status;

    public static ProductOriginDto toDto(ProductOrigin productOrigin) {
        return ProductOriginDto.builder()
                .id(productOrigin.getId())
                .name(productOrigin.getName())
                .description(productOrigin.getDescription())
                .note(productOrigin.getNote())
                .status(productOrigin.getStatus())
                .build();
    }
}
