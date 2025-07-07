package vubq.warehouse_management.VT_EcoStorage.controllers;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import vubq.warehouse_management.VT_EcoStorage.dtos.CustomerDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.ExportOrderDto;
import vubq.warehouse_management.VT_EcoStorage.entities.Customer;
import vubq.warehouse_management.VT_EcoStorage.entities.ExportOrder;
import vubq.warehouse_management.VT_EcoStorage.services.CustomerService;
import vubq.warehouse_management.VT_EcoStorage.services.ExportOrderService;
import vubq.warehouse_management.VT_EcoStorage.utils.https.DataTableRequest;
import vubq.warehouse_management.VT_EcoStorage.utils.https.DataTableResponse;
import vubq.warehouse_management.VT_EcoStorage.utils.https.Response;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final ExportOrderService exportOrderService;

    @GetMapping("/list")
    public Response getCustomers(@NonNull DataTableRequest dataTableRequest) {
        Page<Customer> results = customerService.getCustomers(dataTableRequest);
        return Response.success(
                DataTableResponse.builder()
                        .list(results.getContent().stream().map(CustomerDto::toDto).toList())
                        .totalRecords(results.getTotalElements())
                        .build()
        );
    }

    @GetMapping("/{id}")
    public Response getCustomer(@PathVariable String id, @NonNull DataTableRequest dataTableRequest) {
        return Response.success(customerService.getCustomer(id, dataTableRequest));
    }

    @GetMapping("/export-orders/{customerId}")
    public Response getExportOrders(@NonNull DataTableRequest dataTableRequest, @PathVariable("customerId") String customerId) {
        Page<ExportOrder> results = exportOrderService.getExportOrders(dataTableRequest, customerId);
        return Response.success(
                DataTableResponse.builder()
                        .list(results.getContent().stream().map(ExportOrderDto::toDto).toList())
                        .totalRecords(results.getTotalElements())
                        .build()
        );
    }

    @PostMapping("/create-or-update")
    public Response createOrUpdateCustomer(@RequestBody CustomerDto customerDto) {
        return Response.success(customerService.createOrUpdateCustomer(customerDto));
    }
}
