package vubq.warehouse_management.VT_EcoStorage.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vubq.warehouse_management.VT_EcoStorage.entities.Supplier;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierDto {

    private String id;
    private String name;
    private Supplier.Status status;

    public static SupplierDto toDto(Supplier supplier) {
        return SupplierDto.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .status(supplier.getStatus())
                .build();
    }
}
