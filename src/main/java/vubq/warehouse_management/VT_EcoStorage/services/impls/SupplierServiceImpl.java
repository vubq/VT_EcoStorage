package vubq.warehouse_management.VT_EcoStorage.services.impls;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vubq.warehouse_management.VT_EcoStorage.dtos.SupplierDto;
import vubq.warehouse_management.VT_EcoStorage.entities.Base;
import vubq.warehouse_management.VT_EcoStorage.entities.Supplier;
import vubq.warehouse_management.VT_EcoStorage.repositories.SupplierRepository;
import vubq.warehouse_management.VT_EcoStorage.services.SupplierService;
import vubq.warehouse_management.VT_EcoStorage.utils.https.DataTableRequest;
import vubq.warehouse_management.VT_EcoStorage.utils.specifications.BaseSpecification;
import vubq.warehouse_management.VT_EcoStorage.utils.specifications.SearchCriteria;
import vubq.warehouse_management.VT_EcoStorage.utils.specifications.SearchOperation;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    @Override
    public Page<Supplier> getSuppliers(DataTableRequest dataTableRequest) {
        PageRequest pageable = dataTableRequest.toPageable();
        BaseSpecification<Supplier> specNameContains = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{Base.Fields.name})
                        .operation(SearchOperation.CONTAINS)
                        .value(dataTableRequest.getFilter().trim().toUpperCase())
                        .build()
        );
        BaseSpecification<Supplier> specCodeContains = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{Supplier.Fields.code})
                        .operation(SearchOperation.CONTAINS)
                        .value(dataTableRequest.getFilter().trim().toUpperCase())
                        .build()
        );
        BaseSpecification<Supplier> specTaxNumberContains = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{Supplier.Fields.taxNumber})
                        .operation(SearchOperation.CONTAINS)
                        .value(dataTableRequest.getFilter().trim().toUpperCase())
                        .build()
        );
        BaseSpecification<Supplier> specPhoneNumberContains = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{Supplier.Fields.phoneNumber})
                        .operation(SearchOperation.CONTAINS)
                        .value(dataTableRequest.getFilter().trim().toUpperCase())
                        .build()
        );
        BaseSpecification<Supplier> specEmailContains = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{Supplier.Fields.email})
                        .operation(SearchOperation.CONTAINS)
                        .value(dataTableRequest.getFilter().trim().toUpperCase())
                        .build()
        );
        BaseSpecification<Supplier> specAddressContains = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{Supplier.Fields.address})
                        .operation(SearchOperation.CONTAINS)
                        .value(dataTableRequest.getFilter().trim().toUpperCase())
                        .build()
        );
        return supplierRepository.findAll(
                Specification.where(specNameContains)
                        .or(specCodeContains)
                        .or(specTaxNumberContains)
                        .or(specPhoneNumberContains)
                        .or(specAddressContains)
                        .or(specEmailContains)
                , pageable);
    }

    @Override
    public SupplierDto createOrUpdateSupplier(SupplierDto supplierDto) {
        Supplier supplier;
        if (StringUtils.isEmpty(supplierDto.getId())) {
            supplier = new Supplier();
            supplier.setStatus(Supplier.Status.ACTIVE);
        } else {
            supplier = supplierRepository.findById(supplierDto.getId())
                    .orElseThrow(() -> new RuntimeException("Nha cung cap khong ton tai: " + supplierDto.getId()));
            supplier.setStatus(supplierDto.getStatus());
        }
        supplier.setName(supplierDto.getName());
        supplier.setCode(supplierDto.getCode());
        supplier.setTaxNumber(supplierDto.getTaxNumber());
        supplier.setPhoneNumber(supplierDto.getPhoneNumber());
        supplier.setEmail(supplierDto.getEmail());
        supplier.setAddress(supplierDto.getAddress());
        supplier.setContactPerson(supplierDto.getContactPerson());
        supplier.setDescription(supplierDto.getDescription());
        supplier.setNote(supplierDto.getNote());
        supplier = supplierRepository.saveAndFlush(supplier);
        return SupplierDto.toDto(supplier);
    }

    @Override
    public SupplierDto getSupplier(String supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId).orElse(null);
        if (supplier == null) {
            throw new RuntimeException("Nha cung cap khong ton tai: " + supplierId);
        }
        return SupplierDto.toDto(supplier);
    }
}
