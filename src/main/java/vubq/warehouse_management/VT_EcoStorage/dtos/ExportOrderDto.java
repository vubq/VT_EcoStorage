package vubq.warehouse_management.VT_EcoStorage.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vubq.warehouse_management.VT_EcoStorage.entities.ExportOrder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExportOrderDto {
    private String id;
    private ExportOrder.Status status;
    private Date expectedDate;
    private Date deliveredDate;
    private BigDecimal totalAmount;
    private ExportOrder.Type type;
    private String customerId;
    private String warehouseId;

    private String note;

    private String customerName;
    private String warehouseName;

    List<ExportOrderDetailDto> details;

    public static ExportOrderDto toDataTable(ExportOrder exportOrder) {
        return ExportOrderDto.builder()
                .id(exportOrder.getId())
                .status(exportOrder.getStatus())
                .expectedDate(exportOrder.getExpectedDate())
                .deliveredDate(exportOrder.getDeliveredDate())
                .totalAmount(exportOrder.getTotalAmount())
                .type(exportOrder.getType())
                .customerName(exportOrder.getCustomer().getName())
                .warehouseName(exportOrder.getWarehouse().getName())
                .build();
    }

    public static ExportOrderDto toDto(ExportOrder exportOrder) {
        return ExportOrderDto.builder()
                .id(exportOrder.getId())
                .status(exportOrder.getStatus())
                .expectedDate(exportOrder.getExpectedDate())
                .deliveredDate(exportOrder.getDeliveredDate())
                .totalAmount(exportOrder.getTotalAmount())
                .type(exportOrder.getType())
                .customerId(exportOrder.getCustomer().getId())
                .warehouseId(exportOrder.getWarehouse().getId())
                .note(exportOrder.getNote())
                .build();
    }
}
