package vubq.warehouse_management.VT_EcoStorage.dtos;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vubq.warehouse_management.VT_EcoStorage.entities.PurchaseOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderDto {

    private String id;
    private PurchaseOrder.Status status;
    private Date expectedDate;
    private Date receivedDate;
    private BigDecimal totalAmount;
    private PurchaseOrder.Type type;
    private String warehouseId;
    private String warehouseFromId;
    private String supplierId;
    private String note;

    private String warehouseName;
    private String warehouseFromName;
    private String supplierName;

    @Valid
    private List<PurchaseOrderDetailDto> details;

    public static PurchaseOrderDto toDataTable(PurchaseOrder purchaseOrder) {
        return PurchaseOrderDto.builder()
                .id(purchaseOrder.getId())
                .status(purchaseOrder.getStatus())
                .expectedDate(purchaseOrder.getExpectedDate())
                .receivedDate(purchaseOrder.getReceivedDate())
                .totalAmount(purchaseOrder.getTotalAmount())
                .type(purchaseOrder.getType())
                .warehouseName(purchaseOrder.getWarehouse().getName())
                .warehouseFromName(purchaseOrder.getWarehouseFrom() != null ? purchaseOrder.getWarehouseFrom().getName() : null)
                .supplierName(purchaseOrder.getSupplier() != null ? purchaseOrder.getSupplier().getName() : null)
                .build();
    }

    public static PurchaseOrderDto toDto(PurchaseOrder purchaseOrder) {
        return PurchaseOrderDto.builder()
                .id(purchaseOrder.getId())
                .status(purchaseOrder.getStatus())
                .expectedDate(purchaseOrder.getExpectedDate())
                .receivedDate(purchaseOrder.getReceivedDate())
                .totalAmount(purchaseOrder.getTotalAmount())
                .type(purchaseOrder.getType())
                .warehouseId(purchaseOrder.getWarehouse().getId())
                .warehouseFromId(purchaseOrder.getWarehouseFrom() != null ? purchaseOrder.getWarehouseFrom().getId() : null)
                .supplierId(purchaseOrder.getSupplier() != null ? purchaseOrder.getSupplier().getId() : null)
                .note(purchaseOrder.getNote())
                .build();
    }
}
