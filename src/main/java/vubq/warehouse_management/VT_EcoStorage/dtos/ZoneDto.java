package vubq.warehouse_management.VT_EcoStorage.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vubq.warehouse_management.VT_EcoStorage.entities.Zone;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZoneDto {
    private String id;
    private Zone.Status status;
    private String warehouseId;
    private String name;
    private String note;
    private String description;

    public static ZoneDto toDto(Zone zone) {
        return ZoneDto.builder()
                .id(zone.getId())
                .status(zone.getStatus())
                .warehouseId(zone.getWarehouseId())
                .name(zone.getName())
                .note(zone.getNote())
                .description(zone.getDescription())
                .build();
    }
}
