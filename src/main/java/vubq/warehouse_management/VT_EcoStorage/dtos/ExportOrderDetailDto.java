package vubq.warehouse_management.VT_EcoStorage.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import vubq.warehouse_management.VT_EcoStorage.entities.ExportOrderDetail;
import vubq.warehouse_management.VT_EcoStorage.entities.PurchaseOrderDetail;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExportOrderDetailDto {
    private String id;
    private Long quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private ExportOrderDetail.Status status;
    private String productId;
    private String exportOrderId;
    private String productBarcode;
    private boolean isDelete;

    private String productUnit;
    private String productName;

    private String location;
    private String locationId;

    public static ExportOrderDetail toEntity(
            ExportOrderDetailDto exportOrderDetailDto,
            String exportOrderId
    ) {
        return ExportOrderDetail.builder()
                .id(StringUtils.isNotBlank(exportOrderDetailDto.getId()) ? exportOrderDetailDto.getId() : null)
                .quantity(exportOrderDetailDto.getQuantity())
                .unitPrice(exportOrderDetailDto.getUnitPrice())
                .totalAmount(exportOrderDetailDto.getUnitPrice().multiply(BigDecimal.valueOf(exportOrderDetailDto.getQuantity())))
                .status(exportOrderDetailDto.getStatus())
                .productId(exportOrderDetailDto.getProductId())
                .exportOrderId(
                        StringUtils.isNotBlank(exportOrderDetailDto.getExportOrderId()) ?
                                exportOrderDetailDto.getExportOrderId() : exportOrderId
                )
                .locationId(exportOrderDetailDto.getLocationId())
                .build();
    }

    public static ExportOrderDetailDto toDto(ExportOrderDetail exportOrderDetail) {
        return ExportOrderDetailDto.builder()
                .id(exportOrderDetail.getId())
                .quantity(exportOrderDetail.getQuantity())
                .unitPrice(exportOrderDetail.getUnitPrice())
                .totalAmount(exportOrderDetail.getTotalAmount())
                .status(exportOrderDetail.getStatus())
                .productId(exportOrderDetail.getProductId())
                .exportOrderId(exportOrderDetail.getExportOrderId())
                .productBarcode(exportOrderDetail.getProduct().getBarcode())
                .productUnit(exportOrderDetail.getProduct().getProductUnit().getName())
                .productName(exportOrderDetail.getProduct().getName())
                .location(exportOrderDetail.getProductByLocation().getLocation())
                .locationId(exportOrderDetail.getProductByLocation().getLocationId())
                .isDelete(false)
                .build();
    }
}
