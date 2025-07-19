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
import vubq.warehouse_management.VT_EcoStorage.dtos.PurchaseOrderDto;
import vubq.warehouse_management.VT_EcoStorage.entities.PurchaseOrder;
import vubq.warehouse_management.VT_EcoStorage.services.PurchaseOrderService;
import vubq.warehouse_management.VT_EcoStorage.utils.https.DataTableRequest;
import vubq.warehouse_management.VT_EcoStorage.utils.https.DataTableResponse;
import vubq.warehouse_management.VT_EcoStorage.utils.https.Response;

import java.util.Collection;

@RestController
@RequestMapping("/api/purchase-order")
@RequiredArgsConstructor
public class PurchaseOrderController {

    final private PurchaseOrderService purchaseOrderService;

    @PreAuthorize("hasAuthority('ADMIN.SUPER') or hasAuthority('PURCHASE_ORDER.VIEW')")
    @GetMapping("/list")
    public Response getListUser(
            @NonNull DataTableRequest dataTableRequest,
            @RequestParam String warehouseId,
            @RequestParam String type,
            @RequestParam String status
    ) {
        Page<PurchaseOrder> results = purchaseOrderService.getListPurchaseOrder(dataTableRequest, warehouseId, type, status);
        return Response.success(
                DataTableResponse.builder()
                        .list(results.getContent().stream().map(PurchaseOrderDto::toDataTable).toList())
                        .totalRecords(results.getTotalElements())
                        .build()
        );
    }

    @GetMapping("/reference-data")
    public Response getReferenceData() {
        return Response.success(
                purchaseOrderService.getReferenceDataPurchaseOrder()
        );
    }

    @PostMapping("/create-or-update")
    public Response createOrUpdatePurchaseOrder(@Valid @RequestBody PurchaseOrderDto purchaseOrderDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        boolean hasEdit = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("PURCHASE_ORDER.EDIT"));
        boolean hasAdd = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("PURCHASE_ORDER.ADD"));
        boolean isSuperAdmin = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN.SUPER"));

        if(!isSuperAdmin) {
            if (!StringUtils.isEmpty(purchaseOrderDto.getId())) {
                if (!hasEdit) {
                    throw new AccessDeniedException("Không có quyền");
                }
            } else {
                if (!hasAdd) {
                    throw new AccessDeniedException("Không có quyền");
                }
            }
        }
        boolean success = purchaseOrderService.createOrUpdatePurchaseOrder(purchaseOrderDto);
        return Response.success(success);
    }

    @PreAuthorize("hasAuthority('ADMIN.SUPER') or hasAuthority('PURCHASE_ORDER.VIEW')")
    @GetMapping("/{purchaseOrderId}")
    public Response getPurchaseOrder(@PathVariable("purchaseOrderId") String purchaseOrderId) {
        return Response.success(purchaseOrderService.getPurchaseOrder(purchaseOrderId));
    }
}
