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
import vubq.warehouse_management.VT_EcoStorage.dtos.CustomerDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.ExportOrderDto;
import vubq.warehouse_management.VT_EcoStorage.entities.Customer;
import vubq.warehouse_management.VT_EcoStorage.entities.ExportOrder;
import vubq.warehouse_management.VT_EcoStorage.services.CustomerService;
import vubq.warehouse_management.VT_EcoStorage.services.ExportOrderService;
import vubq.warehouse_management.VT_EcoStorage.utils.https.DataTableRequest;
import vubq.warehouse_management.VT_EcoStorage.utils.https.DataTableResponse;
import vubq.warehouse_management.VT_EcoStorage.utils.https.Response;

import java.util.Collection;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final ExportOrderService exportOrderService;

    @PreAuthorize("hasAuthority('ADMIN.SUPER') or hasAuthority('CUSTOMER.VIEW')")
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

    @PreAuthorize("hasAuthority('ADMIN.SUPER') or hasAuthority('CUSTOMER.VIEW')")
    @GetMapping("/{id}")
    public Response getCustomer(@PathVariable String id, @NonNull DataTableRequest dataTableRequest) {
        return Response.success(customerService.getCustomer(id, dataTableRequest));
    }

    @PreAuthorize("hasAuthority('ADMIN.SUPER') or hasAuthority('CUSTOMER.VIEW')")
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        boolean hasEdit = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("CUSTOMER.EDIT"));
        boolean hasAdd = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("CUSTOMER.ADD"));
        boolean isSuperAdmin = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN.SUPER"));

        if(!isSuperAdmin) {
            if (!StringUtils.isEmpty(customerDto.getId())) {
                if (!hasEdit) {
                    throw new AccessDeniedException("Không có quyền");
                }
            } else {
                if (!hasAdd) {
                    throw new AccessDeniedException("Không có quyền");
                }
            }
        }
        return Response.success(customerService.createOrUpdateCustomer(customerDto));
    }
}
