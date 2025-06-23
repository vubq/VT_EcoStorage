package vubq.warehouse_management.VT_EcoStorage.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vubq.warehouse_management.VT_EcoStorage.entities.Warehouse;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseDto {
    private String id;
    private String address;
    private Warehouse.Status status;
    private String companyId;
    private String name;
    private String note;
    private String description;

    private List<ZoneDto> zones;

    public static WarehouseDto toDto(Warehouse warehouse) {
        return WarehouseDto.builder()
                .id(warehouse.getId())
                .address(warehouse.getAddress())
                .status(warehouse.getStatus())
                .companyId(warehouse.getCompanyId())
                .name(warehouse.getName())
                .note(warehouse.getNote())
                .description(warehouse.getDescription())
                .build();
    }
}
