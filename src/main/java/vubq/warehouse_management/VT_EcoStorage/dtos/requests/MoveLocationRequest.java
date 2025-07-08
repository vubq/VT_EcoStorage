package vubq.warehouse_management.VT_EcoStorage.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoveLocationRequest {

    private  String productId;
    private String locationId;
    private List<LocationNew> locationsNew;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationNew {
        private String locationId;
        private Long quantity;
        private String productId;
    }
}
