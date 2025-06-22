package vubq.warehouse_management.VT_EcoStorage.dtos;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vubq.warehouse_management.VT_EcoStorage.entities.PurchaseOrder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderDto {

    private String Id;
    private PurchaseOrder.Status status;
    private Date expectedDate;
    private Date receivedDate;
    private BigDecimal totalAmount;
    private PurchaseOrder.Type type;
    private String warehouseId;
    private String supplierId;
    private String note;

    private String warehouseName;
    private String supplierName;

    @Valid
    private List<PurchaseOrderDetailDto> details;

    public static PurchaseOrderDto toDataTable(PurchaseOrder purchaseOrder) {
        return PurchaseOrderDto.builder()
                .Id(purchaseOrder.getId())
                .status(purchaseOrder.getStatus())
                .expectedDate(purchaseOrder.getExpectedDate())
                .receivedDate(purchaseOrder.getReceivedDate())
                .totalAmount(purchaseOrder.getTotalAmount())
                .type(purchaseOrder.getType())
                .warehouseName(purchaseOrder.getWarehouse().getName())
                .supplierName(purchaseOrder.getSupplier().getName())
                .build();
    }

    public static PurchaseOrderDto toDto(PurchaseOrder purchaseOrder) {
        return PurchaseOrderDto.builder()
                .Id(purchaseOrder.getId())
                .status(purchaseOrder.getStatus())
                .expectedDate(purchaseOrder.getExpectedDate())
                .receivedDate(purchaseOrder.getReceivedDate())
                .totalAmount(purchaseOrder.getTotalAmount())
                .type(purchaseOrder.getType())
                .warehouseId(purchaseOrder.getWarehouse().getId())
                .supplierId(purchaseOrder.getSupplier().getId())
                .note(purchaseOrder.getNote())
                .build();
    }
}
