package vubq.warehouse_management.VT_EcoStorage.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder
public class Product extends Base {

    @Id
    private String id;

    private String sku;
    private String barcode;
    private String imageUrl;
    private BigDecimal costPrice;
    private BigDecimal salePrice;
    private BigDecimal discountPrice;
    private BigDecimal taxRate;

    @Enumerated(EnumType.STRING)
    private Status status;
    private Long inventoryQuantity;

    @Column(name = "product_category_id")
    private String productCategoryId;

    @Column(name = "product_origin_id")
    private String productOriginId;

    @Column(name = "product_unit_id")
    private String productUnitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_category_id",
            insertable = false,
            updatable = false
    )
    private ProductCategory productCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_origin_id",
            insertable = false,
            updatable = false
    )
    private ProductOrigin productOrigin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_unit_id",
            insertable = false,
            updatable = false
    )
    private ProductUnit productUnit;

    public enum Status {
        ACTIVE,
        INACTIVE
    }
}
