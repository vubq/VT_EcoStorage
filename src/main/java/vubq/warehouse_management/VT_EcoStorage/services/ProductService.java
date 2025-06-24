package vubq.warehouse_management.VT_EcoStorage.services;

import org.springframework.data.domain.Page;
import vubq.warehouse_management.VT_EcoStorage.dtos.*;
import vubq.warehouse_management.VT_EcoStorage.dtos.requests.ProductFilterRequest;
import vubq.warehouse_management.VT_EcoStorage.dtos.requests.ProductInventoryByLocationFilterRequest;
import vubq.warehouse_management.VT_EcoStorage.dtos.responses.ReferenceDataProductResponse;
import vubq.warehouse_management.VT_EcoStorage.entities.*;
import vubq.warehouse_management.VT_EcoStorage.entities.views.ProductByLocation;
import vubq.warehouse_management.VT_EcoStorage.utils.https.DataTableRequest;

import java.util.Date;
import java.util.List;

public interface ProductService {

    ReferenceDataProductResponse getReferenceDataProduct();

    boolean createOrUpdateProduct(ProductDto productDto);

    Page<Product> getListProduct(
            DataTableRequest dataTableRequest,
            ProductFilterRequest productFilterRequest
    );

    ProductDto getProduct(String productId);

    ProductCategoryDto createOrUpdateProductCategory(ProductCategoryDto productCategoryDto);

    Page<ProductCategory> getListProductCategory(DataTableRequest dataTableRequest);

    ProductUnitDto createOrUpdateProductUnit(ProductUnitDto productUnitDto);

    Page<ProductUnit> getListProductUnit(DataTableRequest dataTableRequest);

    ProductOriginDto createOrUpdateProductOrigin(ProductOriginDto productOriginDto);

    Page<ProductOrigin> getListProductOrigin(DataTableRequest dataTableRequest);

    Page<ProductInventory> getListProductInventory(DataTableRequest dataTableRequest, String productId);

    Page<ProductByLocation> getListProductInventoryByLocation(DataTableRequest dataTableRequest, ProductInventoryByLocationFilterRequest productInventoryByLocationFilterRequest);

    List<ProductByLocation> getListProductInventoryByLocation(String locationId);

    List<SummaryDto> statisticalProduct(Date from, Date to);
}
