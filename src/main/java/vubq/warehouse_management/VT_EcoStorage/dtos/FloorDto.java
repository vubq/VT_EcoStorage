package vubq.warehouse_management.VT_EcoStorage.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vubq.warehouse_management.VT_EcoStorage.entities.Floor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FloorDto {
    private String id;
    private Long floor;
    private Floor.Status status;
    private String shelfId;
    private String name;
    private String description;
    private String note;
    private Long quantity;

    public static FloorDto toDto(Floor floor) {
        return FloorDto.builder()
                .id(floor.getId())
                .floor(floor.getFloor())
                .status(floor.getStatus())
                .shelfId(floor.getShelfId())
                .name(floor.getName())
                .description(floor.getDescription())
                .note(floor.getNote())
                .build();
    }
}
