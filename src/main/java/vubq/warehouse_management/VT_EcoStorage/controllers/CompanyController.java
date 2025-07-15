package vubq.warehouse_management.VT_EcoStorage.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vubq.warehouse_management.VT_EcoStorage.dtos.CompanyDto;
import vubq.warehouse_management.VT_EcoStorage.services.CompanyService;
import vubq.warehouse_management.VT_EcoStorage.utils.https.Response;

@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PreAuthorize("hasAuthority('ADMIN.SUPER') or hasAuthority('COMPANY.EDIT')")
    @PostMapping("/create-or-update")
    public Response createOrUpdateCustomer(@RequestBody CompanyDto companyDto) {
        return Response.success(companyService.createOrUpdateCompany(companyDto));
    }

    @PreAuthorize("hasAuthority('ADMIN.SUPER') or hasAuthority('COMPANY.VIEW')")
    @GetMapping()
    public Response getCustomer() {
        return Response.success(companyService.getCompany());
    }
}
