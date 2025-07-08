package vubq.warehouse_management.VT_EcoStorage.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;

@Entity
@Table(name = "tb_product_inventory_location_histories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Builder
public class ProductInventoryLocationHistory extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(name = "product_inventory_location_id")
    private String productInventoryLocationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_inventory_location_id",
            insertable = false,
            updatable = false
    )
    private ProductInventoryLocation productInventoryLocation;

    @Column(name = "export_order_detail_id")
    private String exportOrderDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "export_order_detail_id",
            insertable = false,
            updatable = false
    )
    private ExportOrderDetail exportOrderDetail;

    @Column(name = "purchase_order_detail_id")
    private String purchaseOrderDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "purchase_order_detail_id",
            insertable = false,
            updatable = false
    )
    private PurchaseOrderDetail purchaseOrderDetail;

    @Enumerated(EnumType.STRING)
    private Type type;

    private Long quantity;

    public enum Type {
        PURCHASE,
        EXPORT,
        MOVE
    }
}
