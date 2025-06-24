package vubq.warehouse_management.VT_EcoStorage.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vubq.warehouse_management.VT_EcoStorage.entities.ProductInventory;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductInventoryDto {
    private String id;
    private Long quantity;
    private ProductInventory.Type type;
    private ProductInventory.TransactionType transactionType;
    private String productId;
    private String purchaseOrderId;
    private String exportOrderId;
    private Date createdAt;

    public static ProductInventoryDto toDto(ProductInventory productInventory) {
        return ProductInventoryDto.builder()
                .id(productInventory.getId())
                .quantity(productInventory.getQuantity())
                .type(productInventory.getType())
                .transactionType(productInventory.getTransactionType())
                .productId(productInventory.getProductId())
                .purchaseOrderId(productInventory.getPurchaseOrderDetail() != null ? productInventory.getPurchaseOrderDetail().getPurchaseOrderId() : null)
                .exportOrderId(productInventory.getExportOrderDetail() != null ? productInventory.getExportOrderDetail().getExportOrderId() : null)
                .createdAt(productInventory.getCreatedAt())
                .build();
    }
}
