package vubq.warehouse_management.VT_EcoStorage.services.impls;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vubq.warehouse_management.VT_EcoStorage.dtos.CustomerDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.ExportOrderDto;
import vubq.warehouse_management.VT_EcoStorage.entities.Base;
import vubq.warehouse_management.VT_EcoStorage.entities.Customer;
import vubq.warehouse_management.VT_EcoStorage.entities.ExportOrder;
import vubq.warehouse_management.VT_EcoStorage.repositories.CustomerRepository;
import vubq.warehouse_management.VT_EcoStorage.repositories.ExportOrderRepository;
import vubq.warehouse_management.VT_EcoStorage.services.CustomerService;
import vubq.warehouse_management.VT_EcoStorage.utils.https.DataTableRequest;
import vubq.warehouse_management.VT_EcoStorage.utils.specifications.BaseSpecification;
import vubq.warehouse_management.VT_EcoStorage.utils.specifications.SearchCriteria;
import vubq.warehouse_management.VT_EcoStorage.utils.specifications.SearchOperation;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final ExportOrderRepository exportOrderRepository;

    @Override
    public Page<Customer> getCustomers(DataTableRequest dataTableRequest) {
        PageRequest pageable = dataTableRequest.toPageable();
        BaseSpecification<Customer> specNameContains = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{Base.Fields.name})
                        .operation(SearchOperation.CONTAINS)
                        .value(dataTableRequest.getFilter().trim().toUpperCase())
                        .build()
        );
        BaseSpecification<Customer> specCodeContains = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{Customer.Fields.code})
                        .operation(SearchOperation.CONTAINS)
                        .value(dataTableRequest.getFilter().trim().toUpperCase())
                        .build()
        );
        BaseSpecification<Customer> specTaxNumberContains = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{Customer.Fields.taxNumber})
                        .operation(SearchOperation.CONTAINS)
                        .value(dataTableRequest.getFilter().trim().toUpperCase())
                        .build()
        );
        BaseSpecification<Customer> specPhoneNumberContains = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{Customer.Fields.phoneNumber})
                        .operation(SearchOperation.CONTAINS)
                        .value(dataTableRequest.getFilter().trim().toUpperCase())
                        .build()
        );
        BaseSpecification<Customer> specEmailContains = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{Customer.Fields.email})
                        .operation(SearchOperation.CONTAINS)
                        .value(dataTableRequest.getFilter().trim().toUpperCase())
                        .build()
        );
        BaseSpecification<Customer> specAddressContains = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{Customer.Fields.address})
                        .operation(SearchOperation.CONTAINS)
                        .value(dataTableRequest.getFilter().trim().toUpperCase())
                        .build()
        );
        return customerRepository.findAll(
                Specification.where(specNameContains)
                        .or(specCodeContains)
                        .or(specTaxNumberContains)
                        .or(specPhoneNumberContains)
                        .or(specAddressContains)
                        .or(specEmailContains)
                , pageable);
    }

    @Override
    public CustomerDto createOrUpdateCustomer(CustomerDto customerDto) {
        Customer customer;
        if (StringUtils.isEmpty(customerDto.getId())) {
            customer = new Customer();
            customer.setStatus(Customer.Status.ACTIVE);
        } else {
            customer = customerRepository.findById(customerDto.getId())
                    .orElseThrow(() -> new RuntimeException("Khach hang khong ton tai: " + customerDto.getId()));
            customer.setStatus(customerDto.getStatus());
        }
        customer.setName(customerDto.getName());
        customer.setCode(customerDto.getCode());
        customer.setTaxNumber(customerDto.getTaxNumber());
        customer.setPhoneNumber(customerDto.getPhoneNumber());
        customer.setEmail(customerDto.getEmail());
        customer.setAddress(customerDto.getAddress());
        customer.setContactPerson(customerDto.getContactPerson());
        customer.setDescription(customerDto.getDescription());
        customer.setNote(customerDto.getNote());
        customer = customerRepository.saveAndFlush(customer);
        return CustomerDto.toDto(customer);
    }

    @Override
    public CustomerDto getCustomer(String customerId, DataTableRequest dataTableRequest) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {
            throw new RuntimeException("Nha cung cap khong ton tai: " + customerId);
        }
        return CustomerDto.toDto(customer);
    }
}
