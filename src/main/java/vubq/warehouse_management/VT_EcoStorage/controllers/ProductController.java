package vubq.warehouse_management.VT_EcoStorage.controllers;

import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import vubq.warehouse_management.VT_EcoStorage.dtos.ProductDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.ProductInventoryDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.UserDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.requests.ProductFilterRequest;
import vubq.warehouse_management.VT_EcoStorage.entities.Product;
import vubq.warehouse_management.VT_EcoStorage.entities.ProductInventory;
import vubq.warehouse_management.VT_EcoStorage.services.ProductService;
import vubq.warehouse_management.VT_EcoStorage.utils.https.DataTableRequest;
import vubq.warehouse_management.VT_EcoStorage.utils.https.DataTableResponse;
import vubq.warehouse_management.VT_EcoStorage.utils.https.Response;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    final private ProductService productService;

    @PostMapping("/list")
    public Response getListUser(@NonNull DataTableRequest dataTableRequest, @RequestBody ProductFilterRequest productFilterRequest) {
        Page<Product> results = productService.getListProduct(dataTableRequest, productFilterRequest);
        return Response.success(
                DataTableResponse.builder()
                        .list(results.getContent().stream().map(ProductDto::toDto).toList())
                        .totalRecords(results.getTotalElements())
                        .build()
        );
    }

    @GetMapping("/reference-data")
    public Response getReferenceData() {
        return Response.success(
                productService.getReferenceDataProduct()
        );
    }

    @GetMapping("/{productId}")
    public Response getProduct(@PathVariable("productId") String productId) {
        return Response.success(
                productService.getProduct(productId)
        );
    }

    @GetMapping("/product-inventory/{productId}")
    public Response getListProductInventory(@NonNull DataTableRequest dataTableRequest, @PathVariable("productId") String productId) {
        Page<ProductInventory> results = productService.getListProductInventory(dataTableRequest, productId);
        return Response.success(
                DataTableResponse.builder()
                        .list(results.getContent().stream().map(ProductInventoryDto::toDto).toList())
                        .totalRecords(results.getTotalElements())
                        .build()
        );
    }

    @PostMapping("/create-or-update")
    public Response createOrUpdateProduct(@Valid @RequestBody ProductDto productDto) {
        return Response.success(productService.createOrUpdateProduct(productDto));
    }
}
