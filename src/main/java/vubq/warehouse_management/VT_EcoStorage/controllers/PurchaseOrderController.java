package vubq.warehouse_management.VT_EcoStorage.controllers;

import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import vubq.warehouse_management.VT_EcoStorage.dtos.PurchaseOrderDto;
import vubq.warehouse_management.VT_EcoStorage.entities.PurchaseOrder;
import vubq.warehouse_management.VT_EcoStorage.services.PurchaseOrderService;
import vubq.warehouse_management.VT_EcoStorage.utils.https.DataTableRequest;
import vubq.warehouse_management.VT_EcoStorage.utils.https.DataTableResponse;
import vubq.warehouse_management.VT_EcoStorage.utils.https.Response;

@RestController
@RequestMapping("/api/purchase-order")
@RequiredArgsConstructor
public class PurchaseOrderController {

    final private PurchaseOrderService purchaseOrderService;

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
        boolean success = purchaseOrderService.createOrUpdatePurchaseOrder(purchaseOrderDto);
        return Response.success(success);
    }

    @GetMapping("/{purchaseOrderId}")
    public Response getPurchaseOrder(@PathVariable("purchaseOrderId") String purchaseOrderId) {
        return Response.success(purchaseOrderService.getPurchaseOrder(purchaseOrderId));
    }
}
