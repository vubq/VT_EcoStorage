package vubq.warehouse_management.VT_EcoStorage.entities.views;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;
import vubq.warehouse_management.VT_EcoStorage.entities.Product;

import java.math.BigDecimal;

@Entity
@Table(name = "vw_product_by_location")
@Getter
@FieldNameConstants
public class ProductByLocation {

    @Id
    private String locationId;
    private String productId;
    private String productBarcode;
    private String productName;
    private BigDecimal productCostPrice;
    private BigDecimal productSalePrice;
    private String productCategoryId;
    private String productCategoryName;
    private String productUnitName;
    private String warehouseId;
    private String warehouseName;
    private String zoneId;
    private String zoneName;
    private String shelfId;
    private String shelfName;
    private String floorId;
    private Long floor;
    private Long inventoryQuantity;
    private String location;
}
