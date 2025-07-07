package vubq.warehouse_management.VT_EcoStorage.controllers;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

@RestController
@RequestMapping("/api/supplier")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;
    private final PurchaseOrderService purchaseOrderService;

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

    @GetMapping("/{id}")
    public Response getSupplier(@PathVariable String id) {
        return Response.success(supplierService.getSupplier(id));
    }

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
        return Response.success(supplierService.createOrUpdateSupplier(supplierDto));
    }
}
