package vubq.warehouse_management.VT_EcoStorage.services;

import org.springframework.data.domain.Page;
import vubq.warehouse_management.VT_EcoStorage.dtos.ProductCategoryDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.ProductDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.ProductOriginDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.ProductUnitDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.requests.ProductFilterRequest;
import vubq.warehouse_management.VT_EcoStorage.dtos.responses.ReferenceDataProductResponse;
import vubq.warehouse_management.VT_EcoStorage.entities.Product;
import vubq.warehouse_management.VT_EcoStorage.entities.ProductCategory;
import vubq.warehouse_management.VT_EcoStorage.entities.ProductOrigin;
import vubq.warehouse_management.VT_EcoStorage.entities.ProductUnit;
import vubq.warehouse_management.VT_EcoStorage.utils.https.DataTableRequest;

public interface ProductService {

    ReferenceDataProductResponse getReferenceDataProduct();

    ProductDto createOrUpdateProduct(ProductDto productDto);

    Page<Product> getListProduct(
            DataTableRequest dataTableRequest,
            ProductFilterRequest productFilterRequest
    );

    ProductCategoryDto createOrUpdateProductCategory(ProductCategoryDto productCategoryDto);

    Page<ProductCategory> getListProductCategory(DataTableRequest dataTableRequest);

    ProductUnitDto createOrUpdateProductUnit(ProductUnitDto productUnitDto);

    Page<ProductUnit> getListProductUnit(DataTableRequest dataTableRequest);

    ProductOriginDto createOrUpdateProductOrigin(ProductOriginDto productOriginDto);

    Page<ProductOrigin> getListProductOrigin(DataTableRequest dataTableRequest);
}
