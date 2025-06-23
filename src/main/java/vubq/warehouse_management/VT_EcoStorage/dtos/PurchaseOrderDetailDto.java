package vubq.warehouse_management.VT_EcoStorage.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import vubq.warehouse_management.VT_EcoStorage.entities.ProductInventoryLocation;
import vubq.warehouse_management.VT_EcoStorage.entities.PurchaseOrderDetail;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderDetailDto {

    private String id;
    private Long quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private PurchaseOrderDetail.Status status;
    private String purchaseOrderId;
    private String productId;
    private String productBarcode;
    private boolean isDelete;

    private String productUnit;
    private String productName;

    @Valid
    private List<ProductInventoryLocationDto> locations;

    public static PurchaseOrderDetail toEntity(
            PurchaseOrderDetailDto purchaseOrderDetailDto,
            String purchaseOrderId
    ) {
        return PurchaseOrderDetail.builder()
                .id(StringUtils.isNotBlank(purchaseOrderDetailDto.getId()) ? purchaseOrderDetailDto.getId() : null)
                .quantity(purchaseOrderDetailDto.getQuantity())
                .unitPrice(purchaseOrderDetailDto.getUnitPrice())
                .totalAmount(purchaseOrderDetailDto.getUnitPrice().multiply(BigDecimal.valueOf(purchaseOrderDetailDto.getQuantity())))
                .status(purchaseOrderDetailDto.getStatus())
                .productId(purchaseOrderDetailDto.getProductId())
                .purchaseOrderId(
                        StringUtils.isNotBlank(purchaseOrderDetailDto.getPurchaseOrderId()) ?
                                purchaseOrderDetailDto.getPurchaseOrderId() : purchaseOrderId
                )
                .build();
    }

    public static PurchaseOrderDetailDto toDto(PurchaseOrderDetail purchaseOrderDetail) {
        return PurchaseOrderDetailDto.builder()
                .id(purchaseOrderDetail.getId())
                .quantity(purchaseOrderDetail.getQuantity())
                .unitPrice(purchaseOrderDetail.getUnitPrice())
                .totalAmount(purchaseOrderDetail.getTotalAmount())
                .status(purchaseOrderDetail.getStatus())
                .productId(purchaseOrderDetail.getProductId())
                .purchaseOrderId(purchaseOrderDetail.getPurchaseOrderId())
                .productBarcode(purchaseOrderDetail.getProduct().getBarcode())
                .productUnit(purchaseOrderDetail.getProduct().getProductUnit().getName())
                .productName(purchaseOrderDetail.getProduct().getName())
                .isDelete(false)
                .build();
    }
}
