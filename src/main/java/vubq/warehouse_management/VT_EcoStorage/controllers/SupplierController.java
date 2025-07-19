package vubq.warehouse_management.VT_EcoStorage.controllers;

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
import vubq.warehouse_management.VT_EcoStorage.dtos.PurchaseOrderDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.SupplierDto;
import vubq.warehouse_management.VT_EcoStorage.entities.PurchaseOrder;
import vubq.warehouse_management.VT_EcoStorage.entities.Supplier;
import vubq.warehouse_management.VT_EcoStorage.services.PurchaseOrderService;
import vubq.warehouse_management.VT_EcoStorage.services.SupplierService;
import vubq.warehouse_management.VT_EcoStorage.utils.https.DataTableRequest;
import vubq.warehouse_management.VT_EcoStorage.utils.https.DataTableResponse;
import vubq.warehouse_management.VT_EcoStorage.utils.https.Response;

import java.util.Collection;

@RestController
@RequestMapping("/api/supplier")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;
    private final PurchaseOrderService purchaseOrderService;

    @PreAuthorize("hasAuthority('ADMIN.SUPER') or hasAuthority('SUPPLIER.VIEW')")
    @GetMapping("/list")
    public Response getSuppliers(@NonNull DataTableRequest dataTableRequest) {
        Page<Supplier> results = supplierService.getSuppliers(dataTableRequest);
        return Response.success(
                DataTableResponse.builder()
                        .list(results.getContent().stream().map(SupplierDto::toDto).toList())
                        .totalRecords(results.getTotalElements())
                        .build()
        );
    }

    @PreAuthorize("hasAuthority('ADMIN.SUPER') or hasAuthority('SUPPLIER.VIEW')")
    @GetMapping("/{id}")
    public Response getSupplier(@PathVariable String id) {
        return Response.success(supplierService.getSupplier(id));
    }

    @PreAuthorize("hasAuthority('ADMIN.SUPER') or hasAuthority('SUPPLIER.VIEW')")
    @GetMapping("/purchase-orders/{supplierId}")
    public Response getExportOrders(@NonNull DataTableRequest dataTableRequest, @PathVariable("supplierId") String supplierId) {
        Page<PurchaseOrder> results = purchaseOrderService.getPurchaseOrders(dataTableRequest, supplierId);
        return Response.success(
                DataTableResponse.builder()
                        .list(results.getContent().stream().map(PurchaseOrderDto::toDto).toList())
                        .totalRecords(results.getTotalElements())
                        .build()
        );
    }

    @PostMapping("/create-or-update")
    public Response createOrUpdateSupplier(@RequestBody SupplierDto supplierDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        boolean hasEdit = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("SUPPLIER.EDIT"));
        boolean hasAdd = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("SUPPLIER.ADD"));
        boolean isSuperAdmin = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN.SUPER"));

        if(!isSuperAdmin) {
            if (!StringUtils.isEmpty(supplierDto.getId())) {
                if (!hasEdit) {
                    throw new AccessDeniedException("Không có quyền");
                }
            } else {
                if (!hasAdd) {
                    throw new AccessDeniedException("Không có quyền");
                }
            }
        }
        return Response.success(supplierService.createOrUpdateSupplier(supplierDto));
    }
}
