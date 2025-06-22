package vubq.warehouse_management.VT_EcoStorage.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import vubq.warehouse_management.VT_EcoStorage.entities.ExportOrderDetail;

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
    private boolean isDelete;

    public static ExportOrderDetail toEntity(
            ExportOrderDetailDto exportOrderDetailDto,
            String exportOrderId
    ) {
        return ExportOrderDetail.builder()
                .id(exportOrderDetailDto.getId())
                .quantity(exportOrderDetailDto.getQuantity())
                .unitPrice(exportOrderDetailDto.getUnitPrice())
                .totalAmount(exportOrderDetailDto.getUnitPrice().multiply(BigDecimal.valueOf(exportOrderDetailDto.getQuantity())))
                .status(exportOrderDetailDto.getStatus())
                .productId(exportOrderDetailDto.getProductId())
                .exportOrderId(
                        StringUtils.isNotBlank(exportOrderDetailDto.getExportOrderId()) ?
                                exportOrderDetailDto.getExportOrderId() : exportOrderId
                )
                .build();
    }
}
