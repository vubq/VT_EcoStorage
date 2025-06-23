package vubq.warehouse_management.VT_EcoStorage.services.impls;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vubq.warehouse_management.VT_EcoStorage.dtos.*;
import vubq.warehouse_management.VT_EcoStorage.dtos.responses.ReferenceDataExportOrderResponse;
import vubq.warehouse_management.VT_EcoStorage.entities.*;
import vubq.warehouse_management.VT_EcoStorage.repositories.*;
import vubq.warehouse_management.VT_EcoStorage.services.ExportOrderService;
import vubq.warehouse_management.VT_EcoStorage.utils.dates.DateUtils;
import vubq.warehouse_management.VT_EcoStorage.utils.https.DataTableRequest;
import vubq.warehouse_management.VT_EcoStorage.utils.specifications.BaseSpecification;
import vubq.warehouse_management.VT_EcoStorage.utils.specifications.SearchCriteria;
import vubq.warehouse_management.VT_EcoStorage.utils.specifications.SearchOperation;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ExportOrderServiceImpl implements ExportOrderService {

    final private ExportOrderRepository exportOrderRepository;
    final private ExportOrderDetailRepository exportOrderDetailRepository;
    final private ProductInventoryRepository productInventoryRepository;
    final private ProductRepository productRepository;
    final private WarehouseRepository warehouseRepository;
    final private SupplierRepository supplierRepository;
    final private ProductCategoryRepository productCategoryRepository;
    final private CustomerRepository customerRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public Long getNextExportOrderSeq() {
        return ((Number) entityManager
                .createNativeQuery("SELECT nextval('export_order_seq')")
                .getSingleResult()).longValue();
    }

    @Override
    public boolean createOrUpdateExportOrder(ExportOrderDto exportOrderDto) {
        if (exportOrderDto.getDetails() == null || exportOrderDto.getDetails().isEmpty()) {
            throw new IllegalArgumentException("No products have been added");
        }
        ExportOrder exportOrder;
        if (StringUtils.isEmpty(exportOrderDto.getId())) {
            exportOrder = new ExportOrder();
            exportOrder.setId(generateExportOrderId(exportOrderDto.getCustomerId()));
            exportOrder.setStatus(ExportOrder.Status.NEW);
            exportOrder.setCustomerId(exportOrderDto.getCustomerId());
            exportOrder.setWarehouseId(exportOrderDto.getWarehouseId());
            exportOrder.setExpectedDate(exportOrderDto.getExpectedDate());
            exportOrder.setType(ExportOrder.Type.EXPORT);

            exportOrder = exportOrderRepository.saveAndFlush(exportOrder);

            ExportOrder finalExportOrder = exportOrder;
            List<ExportOrderDetail> exportOrderDetails = exportOrderDto.getDetails().stream()
                    .filter(detail -> !detail.isDelete())
                    .map(exportOrderDetailDto -> ExportOrderDetailDto.toEntity(exportOrderDetailDto, finalExportOrder.getId()))
                    .toList();

            exportOrder.setTotalAmount(
                    exportOrderDetails.stream()
                            .map(ExportOrderDetail::getTotalAmount)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
            );
            exportOrderDetailRepository.saveAll(exportOrderDetails);

            return true;
        } else {
            exportOrder = exportOrderRepository.findById(exportOrderDto.getId())
                    .orElseThrow(() -> new RuntimeException("Export order not found with id: " + exportOrderDto.getId()));
        }

        if (exportOrder.getStatus() == ExportOrder.Status.NEW && exportOrderDto.getStatus() == ExportOrder.Status.CANCELED) {
            exportOrder.setStatus(ExportOrder.Status.CANCELED);
            exportOrderRepository.saveAndFlush(exportOrder);

            return true;
        }

        if (exportOrder.getStatus() == ExportOrder.Status.NEW && exportOrderDto.getStatus() == ExportOrder.Status.CONFIRMED) {
            exportOrder.setStatus(ExportOrder.Status.CONFIRMED);
            exportOrder.setCustomerId(exportOrderDto.getCustomerId());
            exportOrder.setWarehouseId(exportOrderDto.getWarehouseId());
            exportOrder.setExpectedDate(exportOrderDto.getExpectedDate());

            ExportOrder finalExportOrder = exportOrder;
            List<ExportOrderDetail> exportOrderDetails = exportOrderDto.getDetails().stream()
                    .filter(detail -> !detail.isDelete())
                    .map(exportOrderDetailDto -> ExportOrderDetailDto.toEntity(exportOrderDetailDto, finalExportOrder.getId()))
                    .toList();

            exportOrder.setTotalAmount(
                    exportOrderDetails.stream()
                            .map(ExportOrderDetail::getTotalAmount)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
            );

            exportOrderRepository.saveAndFlush(exportOrder);
            exportOrderDetailRepository.saveAll(exportOrderDetails);

            List<String> purchaseOrderDetailDeleteIds = exportOrderDto.getDetails().stream()
                    .filter(ExportOrderDetailDto::isDelete)
                    .map(ExportOrderDetailDto::getId)
                    .filter(StringUtils::isNotBlank)
                    .toList();
            exportOrderDetailRepository.deleteByIdIn(purchaseOrderDetailDeleteIds);

            return true;
        }

        if (exportOrder.getStatus() == ExportOrder.Status.CONFIRMED && exportOrderDto.getStatus() == ExportOrder.Status.DELIVERED) {
            exportOrder.setStatus(ExportOrder.Status.DELIVERED);
            exportOrder.setDeliveredDate(DateUtils.getCurrentTime());

            List<String> productIds = exportOrderDto.getDetails().stream()
                    .map(ExportOrderDetailDto::getProductId)
                    .toList();
            List<Product> products = productRepository.findByIdIn(productIds);
            Map<String, Product> productMap = products.stream()
                    .collect(Collectors.toMap(Product::getId, p -> p));
            List<Product> productsUpdate = new ArrayList<>();

            List<ProductInventory> productInventories = exportOrderDto.getDetails().stream()
                    .map(exportOrderDetail -> {
                        ProductInventory productInventory = new ProductInventory();
                        productInventory.setType(ProductInventory.Type.EXPORT_ORDER);
                        productInventory.setTransactionType(ProductInventory.TransactionType.SUBTRACT);
                        productInventory.setQuantity(exportOrderDetail.getQuantity());
                        productInventory.setProductId(exportOrderDetail.getProductId());
                        productInventory.setExportOrderDetailId(exportOrderDetail.getId());

                        Product productUpdate = productMap.get(exportOrderDetail.getProductId());
                        productUpdate.setInventoryQuantity(productUpdate.getInventoryQuantity() - exportOrderDetail.getQuantity());
                        productsUpdate.add(productUpdate);

                        return productInventory;
                    })
                    .toList();
            productInventoryRepository.saveAllAndFlush(productInventories);
            productRepository.saveAllAndFlush(productsUpdate);
        }

        return false;
    }

    @Override
    public ReferenceDataExportOrderResponse getReferenceDataExportOrder() {
        ReferenceDataExportOrderResponse referenceDataExportOrderResponse = new ReferenceDataExportOrderResponse();
        referenceDataExportOrderResponse.setWarehouses(warehouseRepository.findByStatus(Warehouse.Status.ACTIVE).stream().map(WarehouseDto::toDto).collect(Collectors.toList()));
        referenceDataExportOrderResponse.setCustomers(customerRepository.findByStatus(Customer.Status.ACTIVE).stream().map(CustomerDto::toDto).collect(Collectors.toList()));
        referenceDataExportOrderResponse.setCategories(productCategoryRepository.findByStatus(ProductCategory.Status.ACTIVE).stream().map(ProductCategoryDto::toDto).collect(Collectors.toList()));
        return referenceDataExportOrderResponse;
    }

    private String generateExportOrderId(String customerId) {
        return getNextExportOrderSeq()
                + "-" + "EO" + "-C"
//                + "-" + customerId
                + "-" + new SimpleDateFormat("yyyyMMdd").format(new Date());
    }

    @Override
    public Page<ExportOrder> getExportOrders(DataTableRequest dataTableRequest) {
        PageRequest pageable = dataTableRequest.toPageable();
        BaseSpecification<ExportOrder> specIdContains = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{ExportOrder.Fields.id})
                        .operation(SearchOperation.CONTAINS)
                        .value(dataTableRequest.getFilter().trim().toUpperCase())
                        .build()
        );
        return exportOrderRepository.findAll(Specification.where(specIdContains), pageable);
    }

    @Override
    public ExportOrderDto getExportOrder(String exportOrderId) {
        ExportOrderDto exportOrderDto = new ExportOrderDto();
        ExportOrder exportOrder = exportOrderRepository.findById(exportOrderId).orElseThrow(() -> new RuntimeException("Export order not found with id: " + exportOrderId));

        List<ExportOrderDetail> exportOrderDetails = exportOrderDetailRepository.findByExportOrderId(exportOrderId);
        exportOrderDto = ExportOrderDto.toDto(exportOrder);
        exportOrderDto.setDetails(exportOrderDetails.stream().map(ExportOrderDetailDto::toDto).collect(Collectors.toList()));
        return exportOrderDto;
    }
}
