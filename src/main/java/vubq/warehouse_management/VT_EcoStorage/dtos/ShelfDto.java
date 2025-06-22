package vubq.warehouse_management.VT_EcoStorage.dtos;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vubq.warehouse_management.VT_EcoStorage.entities.Shelf;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShelfDto {
    private String id;
    private Shelf.Status status;
    private String zoneId;
    private String name;
    private String description;
    private String note;

    public static ShelfDto toDto(Shelf shelf) {
        return ShelfDto.builder()
                .id(shelf.getId())
                .status(shelf.getStatus())
                .zoneId(shelf.getZoneId())
                .name(shelf.getName())
                .description(shelf.getDescription())
                .build();
    }
}
