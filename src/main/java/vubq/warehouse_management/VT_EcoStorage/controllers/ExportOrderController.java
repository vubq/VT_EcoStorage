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
import vubq.warehouse_management.VT_EcoStorage.dtos.ExportOrderDto;
import vubq.warehouse_management.VT_EcoStorage.entities.ExportOrder;
import vubq.warehouse_management.VT_EcoStorage.services.ExportOrderService;
import vubq.warehouse_management.VT_EcoStorage.utils.https.DataTableRequest;
import vubq.warehouse_management.VT_EcoStorage.utils.https.DataTableResponse;
import vubq.warehouse_management.VT_EcoStorage.utils.https.Response;

import java.util.Collection;

@RestController
@RequestMapping("/api/export-order")
@RequiredArgsConstructor
public class ExportOrderController {

    final private ExportOrderService exportOrderService;

    @PreAuthorize("hasAuthority('ADMIN.SUPER') or hasAuthority('EXPORT_ORDER.VIEW')")
    @GetMapping("/list")
    public Response getListUser(
            @NonNull DataTableRequest dataTableRequest,
            @RequestParam String warehouseId,
            @RequestParam String type,
            @RequestParam String status
    ) {
        Page<ExportOrder> results = exportOrderService.getExportOrders(dataTableRequest, warehouseId, type, status);
        return Response.success(
                DataTableResponse.builder()
                        .list(results.getContent().stream().map(ExportOrderDto::toDataTable).toList())
                        .totalRecords(results.getTotalElements())
                        .build()
        );
    }

    @GetMapping("/reference-data")
    public Response getReferenceData() {
        return Response.success(
                exportOrderService.getReferenceDataExportOrder()
        );
    }

    @PostMapping("/create-or-update")
    public Response createOrUpdateExportOrder(@Valid @RequestBody ExportOrderDto exportOrderDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        boolean hasEdit = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("EXPORT_ORDER.EDIT"));
        boolean hasAdd = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("EXPORT_ORDER.ADD"));
        boolean isSuperAdmin = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN.SUPER"));

        if(!isSuperAdmin) {
            if (!StringUtils.isEmpty(exportOrderDto.getId())) {
                if (!hasEdit) {
                    throw new AccessDeniedException("Không có quyền");
                }
            } else {
                if (!hasAdd) {
                    throw new AccessDeniedException("Không có quyền");
                }
            }
        }

        boolean success = exportOrderService.createOrUpdateExportOrder(exportOrderDto);
        return Response.success(success);
    }

    @PreAuthorize("hasAuthority('ADMIN.SUPER') or hasAuthority('EXPORT_ORDER.VIEW')")
    @GetMapping("/{exportOrderId}")
    public Response getExportOrder(@PathVariable("exportOrderId") String exportOrderId) {
        return Response.success(exportOrderService.getExportOrder(exportOrderId));
    }
}
