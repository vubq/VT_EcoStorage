package vubq.warehouse_management.VT_EcoStorage.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vubq.warehouse_management.VT_EcoStorage.entities.Product;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private String id;
    private String sku;
    private String barcode;
    private String name;
    private String imageUrl;
    private String description;
    private BigDecimal costPrice;
    private BigDecimal salePrice;
    private BigDecimal discountPrice;
    private BigDecimal taxRate;
    private String note;
    private Product.Status status;
    private String productCategoryId;
    private String productOriginId;
    private String productUnitId;

    private String productUnitName;
    private String productCategoryName;
    private Long inventoryQuantity;

    private List<ProductInventoryDto> historyInventories;

    public static ProductDto toDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .sku(product.getSku())
                .barcode(product.getBarcode())
                .name(product.getName())
                .imageUrl(product.getImageUrl())
                .description(product.getDescription())
                .costPrice(product.getCostPrice())
                .salePrice(product.getSalePrice())
                .discountPrice(product.getDiscountPrice())
                .taxRate(product.getTaxRate())
                .note(product.getNote())
                .status(product.getStatus())
                .productCategoryId(product.getProductCategoryId())
                .productOriginId(product.getProductOriginId())
                .productUnitId(product.getProductUnitId())
                .productUnitName(product.getProductUnit().getName())
                .productCategoryName(product.getProductCategory().getName())
                .inventoryQuantity(product.getInventoryQuantity())
                .build();
    }
}
