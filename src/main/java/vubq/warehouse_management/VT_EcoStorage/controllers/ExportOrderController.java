package vubq.warehouse_management.VT_EcoStorage.controllers;

import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import vubq.warehouse_management.VT_EcoStorage.dtos.ExportOrderDto;
import vubq.warehouse_management.VT_EcoStorage.entities.ExportOrder;
import vubq.warehouse_management.VT_EcoStorage.services.ExportOrderService;
import vubq.warehouse_management.VT_EcoStorage.utils.https.DataTableRequest;
import vubq.warehouse_management.VT_EcoStorage.utils.https.DataTableResponse;
import vubq.warehouse_management.VT_EcoStorage.utils.https.Response;

@RestController
@RequestMapping("/api/export-order")
@RequiredArgsConstructor
public class ExportOrderController {

    final private ExportOrderService exportOrderService;

    @GetMapping("/list")
    public Response getListUser(@NonNull DataTableRequest dataTableRequest) {
        Page<ExportOrder> results = exportOrderService.getExportOrders(dataTableRequest);
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
        boolean success = exportOrderService.createOrUpdateExportOrder(exportOrderDto);
        return Response.success(success);
    }

    @GetMapping("/{exportOrderId}")
    public Response getExportOrder(@PathVariable("exportOrderId") String exportOrderId) {
        return Response.success(exportOrderService.getExportOrder(exportOrderId));
    }
}
