package vubq.warehouse_management.VT_EcoStorage.services;

import org.springframework.data.domain.Page;
import vubq.warehouse_management.VT_EcoStorage.dtos.CustomerDto;
import vubq.warehouse_management.VT_EcoStorage.entities.Customer;
import vubq.warehouse_management.VT_EcoStorage.utils.https.DataTableRequest;

public interface CustomerService {

    Page<Customer> getCustomers(DataTableRequest dataTableRequest);

    CustomerDto createOrUpdateCustomer(CustomerDto customerDto);

    CustomerDto getCustomer(String customerId, DataTableRequest dataTableRequest);
}
