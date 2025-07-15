package vubq.warehouse_management.VT_EcoStorage.services;

import vubq.warehouse_management.VT_EcoStorage.dtos.CompanyDto;

public interface CompanyService {

    CompanyDto createOrUpdateCompany(CompanyDto companyDto);

    CompanyDto getCompany();
}
