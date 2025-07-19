package vubq.warehouse_management.VT_EcoStorage.controllers;

import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vubq.warehouse_management.VT_EcoStorage.dtos.*;
import vubq.warehouse_management.VT_EcoStorage.dtos.requests.ProductFilterRequest;
import vubq.warehouse_management.VT_EcoStorage.dtos.requests.ProductInventoryByLocationFilterRequest;
import vubq.warehouse_management.VT_EcoStorage.entities.*;
import vubq.warehouse_management.VT_EcoStorage.entities.views.ProductByLocation;
import vubq.warehouse_management.VT_EcoStorage.services.ProductService;
import vubq.warehouse_management.VT_EcoStorage.utils.https.DataTableRequest;
import vubq.warehouse_management.VT_EcoStorage.utils.https.DataTableResponse;
import vubq.warehouse_management.VT_EcoStorage.utils.https.Response;

import java.util.Collection;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    final private ProductService productService;

    @PreAuthorize("hasAuthority('ADMIN.SUPER') or hasAuthority('PRODUCT.VIEW')")
    @PostMapping("/list")
    public Response getListProduct(@NonNull DataTableRequest dataTableRequest, @RequestBody ProductFilterRequest productFilterRequest) {
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

    @PreAuthorize("hasAuthority('ADMIN.SUPER') or hasAuthority('PRODUCT.VIEW')")
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        boolean hasEdit = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("PRODUCT.EDIT"));
        boolean hasAdd = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("PRODUCT.ADD"));
        boolean isSuperAdmin = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN.SUPER"));

        if(!isSuperAdmin) {
            if (!StringUtils.isEmpty(productDto.getId())) {
                if (!hasEdit) {
                    throw new AccessDeniedException("Không có quyền");
                }
            } else {
                if (!hasAdd) {
                    throw new AccessDeniedException("Không có quyền");
                }
            }
        }
        return Response.success(productService.createOrUpdateProduct(productDto));
    }

    @PostMapping("/product-by-inventory-location")
    public Response getListProductInventoryByLocation(@NonNull DataTableRequest dataTableRequest, @RequestBody ProductInventoryByLocationFilterRequest productInventoryByLocationFilterRequest) {
        Page<ProductByLocation> results = productService.getListProductInventoryByLocation(dataTableRequest, productInventoryByLocationFilterRequest);
        return Response.success(
                DataTableResponse.builder()
                        .list(results.getContent())
                        .totalRecords(results.getTotalElements())
                        .build()
        );
    }

    @GetMapping("/product-by-inventory-location/{locationId}")
    public Response getListProductInventoryByLocation(@PathVariable("locationId") String locationId) {
        return Response.success(productService.getListProductInventoryByLocation(locationId));
    }

    @PreAuthorize("hasAuthority('ADMIN.SUPER') or hasAuthority('CATEGORY.VIEW')")
    @GetMapping("/category/list")
    public Response getListCategory(@NonNull DataTableRequest dataTableRequest) {
        Page<ProductCategory> results = productService.getListProductCategory(dataTableRequest);
        return Response.success(
                DataTableResponse.builder()
                        .list(results.getContent().stream().map(ProductCategoryDto::toDto).toList())
                        .totalRecords(results.getTotalElements())
                        .build()
        );
    }

    @PreAuthorize("hasAuthority('ADMIN.SUPER') or hasAuthority('CATEGORY.VIEW')")
    @GetMapping("/category/{id}")
    public Response getProductCategory(@PathVariable String id) {
        return Response.success(productService.getProductCategoryById(id));
    }

    @PostMapping("/category/create-or-update")
    public Response createOrUpdateCategory(@RequestBody ProductCategoryDto productCategoryDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        boolean hasEdit = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("CATEGORY.EDIT"));
        boolean hasAdd = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("CATEGORY.ADD"));
        boolean isSuperAdmin = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN.SUPER"));

        if(!isSuperAdmin) {
            if (!StringUtils.isEmpty(productCategoryDto.getId())) {
                if (!hasEdit) {
                    throw new AccessDeniedException("Không có quyền");
                }
            } else {
                if (!hasAdd) {
                    throw new AccessDeniedException("Không có quyền");
                }
            }
        }
        return Response.success(productService.createOrUpdateProductCategory(productCategoryDto));
    }

    @PreAuthorize("hasAuthority('ADMIN.SUPER') or hasAuthority('UNIT.VIEW')")
    @GetMapping("/unit/list")
    public Response getListUnit(@NonNull DataTableRequest dataTableRequest) {
        Page<ProductUnit> results = productService.getListProductUnit(dataTableRequest);
        return Response.success(
                DataTableResponse.builder()
                        .list(results.getContent().stream().map(ProductUnitDto::toDto).toList())
                        .totalRecords(results.getTotalElements())
                        .build()
        );
    }

    @PreAuthorize("hasAuthority('ADMIN.SUPER') or hasAuthority('UNIT.VIEW')")
    @GetMapping("/unit/{id}")
    public Response getUnit(@PathVariable String id) {
        return Response.success(productService.getProductUnitById(id));
    }

    @PostMapping("/unit/create-or-update")
    public Response createOrUpdateUnit(@RequestBody ProductUnitDto productUnitDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        boolean hasEdit = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("UNIT.EDIT"));
        boolean hasAdd = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("UNIT.ADD"));
        boolean isSuperAdmin = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN.SUPER"));

        if(!isSuperAdmin) {
            if (!StringUtils.isEmpty(productUnitDto.getId())) {
                if (!hasEdit) {
                    throw new AccessDeniedException("Không có quyền");
                }
            } else {
                if (!hasAdd) {
                    throw new AccessDeniedException("Không có quyền");
                }
            }
        }
        return Response.success(productService.createOrUpdateProductUnit(productUnitDto));
    }

    @PreAuthorize("hasAuthority('ADMIN.SUPER') or hasAuthority('ORIGIN.VIEW')")
    @GetMapping("/origin/list")
    public Response getListOrigin(@NonNull DataTableRequest dataTableRequest) {
        Page<ProductOrigin> results = productService.getListProductOrigin(dataTableRequest);
        return Response.success(
                DataTableResponse.builder()
                        .list(results.getContent().stream().map(ProductOriginDto::toDto).toList())
                        .totalRecords(results.getTotalElements())
                        .build()
        );
    }

    @PreAuthorize("hasAuthority('ADMIN.SUPER') or hasAuthority('ORIGIN.VIEW')")
    @GetMapping("/origin/{id}")
    public Response getOrigin(@PathVariable String id) {
        return Response.success(productService.getProductOriginById(id));
    }

    @PostMapping("/origin/create-or-update")
    public Response createOrUpdateOrigin(@RequestBody ProductOriginDto productOriginDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        boolean hasEdit = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ORIGIN.EDIT"));
        boolean hasAdd = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ORIGIN.ADD"));
        boolean isSuperAdmin = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN.SUPER"));

        if(!isSuperAdmin) {
            if (!StringUtils.isEmpty(productOriginDto.getId())) {
                if (!hasEdit) {
                    throw new AccessDeniedException("Không có quyền");
                }
            } else {
                if (!hasAdd) {
                    throw new AccessDeniedException("Không có quyền");
                }
            }
        }
        return Response.success(productService.createOrUpdateProductOrigin(productOriginDto));
    }
}
