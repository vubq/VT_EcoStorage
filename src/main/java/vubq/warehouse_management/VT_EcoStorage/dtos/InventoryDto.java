package vubq.warehouse_management.VT_EcoStorage.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDto {

    private String warehouseId;
    private String warehouseName;

    private List<Product> products;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Product {
        private String productId;
        private String productName;
        private String productBarcode;
        private String productSKU;
        private Long inventoryQuantity;

        private List<Location> locations;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Location {
            private String locationId;
            private String locationName;
            private Long inventoryQuantity;
        }
    }
}
