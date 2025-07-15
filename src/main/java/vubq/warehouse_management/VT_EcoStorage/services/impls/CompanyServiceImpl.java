package vubq.warehouse_management.VT_EcoStorage.services.impls;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import vubq.warehouse_management.VT_EcoStorage.dtos.CompanyDto;
import vubq.warehouse_management.VT_EcoStorage.entities.Company;
import vubq.warehouse_management.VT_EcoStorage.repositories.CompanyRepository;
import vubq.warehouse_management.VT_EcoStorage.services.CompanyService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    @Override
    public CompanyDto createOrUpdateCompany(CompanyDto companyDto) {
        Company company;
        if (StringUtils.isEmpty(companyDto.getId())) {
            company = new Company();
            company.setStatus(Company.Status.ACTIVE);
        } else {
            company = companyRepository.findById(companyDto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Không có thông tin công ty"));
        }
        company.setName(companyDto.getName());
        company.setCode(companyDto.getCode());
        company.setTaxNumber(companyDto.getTaxNumber());
        company.setPhoneNumber(companyDto.getPhoneNumber());
        company.setEmail(companyDto.getEmail());
        company.setAddress(companyDto.getAddress());
        company.setContactPerson(companyDto.getContactPerson());
        company.setDescription(companyDto.getDescription());
        company.setNote(companyDto.getNote());
        company = companyRepository.saveAndFlush(company);
        return CompanyDto.toDto(company);
    }

    @Override
    public CompanyDto getCompany() {
        List<Company> companies = companyRepository.findAll();
        if (companies.isEmpty()) {
            return null;
        } else {
            return CompanyDto.toDto(companyRepository.findAll().get(0));
        }
    }
}
