package vubq.warehouse_management.VT_EcoStorage.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductStatisticalDto {

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
        private Long totalImportQuantity;
        private Double totalImportAmount;
        private Long totalExportQuantity;
        private Double totalExportAmount;
    }
}
