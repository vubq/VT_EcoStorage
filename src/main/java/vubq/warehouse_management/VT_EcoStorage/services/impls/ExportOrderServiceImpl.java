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
    final private ProductInventoryLocationRepository productInventoryLocationRepository;
    final private ProductInventoryLocationHistoryRepository productInventoryLocationHistoryRepository;
    final private PurchaseOrderRepository purchaseOrderRepository;
    final private PurchaseOrderDetailRepository purchaseOrderDetailRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public Long getNextExportOrderSeq() {
        return ((Number) entityManager
                .createNativeQuery("SELECT nextval('export_order_seq')")
                .getSingleResult()).longValue();
    }

    public Long getNextPurchaseOrderSeq() {
        return ((Number) entityManager.createNativeQuery("SELECT nextval('purchase_order_seq')").getSingleResult()).longValue();
    }

    @Override
    public boolean createOrUpdateExportOrder(ExportOrderDto exportOrderDto) {
        if (exportOrderDto.getDetails() == null || exportOrderDto.getDetails().isEmpty()) {
            if (exportOrderDto.getStatus() != ExportOrder.Status.DELIVERED) {
                throw new IllegalArgumentException("No products have been added");
            }
        }
        ExportOrder exportOrder;
        if (StringUtils.isEmpty(exportOrderDto.getId())) {
            exportOrder = new ExportOrder();
            exportOrder.setId(generateExportOrderId(exportOrderDto.getCustomerId()));
            exportOrder.setStatus(ExportOrder.Status.NEW);
            exportOrder.setWarehouseId(exportOrderDto.getWarehouseId());
            exportOrder.setExpectedDate(exportOrderDto.getExpectedDate());
            exportOrder.setWarehouseToId(StringUtils.isEmpty(exportOrderDto.getWarehouseToId()) ? null : exportOrderDto.getWarehouseToId());
            exportOrder.setCustomerId(StringUtils.isEmpty(exportOrderDto.getCustomerId()) ? null : exportOrderDto.getCustomerId());
            exportOrder.setType(exportOrderDto.getType());

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

        if (exportOrder.getStatus() == ExportOrder.Status.NEW && exportOrderDto.getStatus() == ExportOrder.Status.NEW) {
            exportOrder.setWarehouseId(exportOrderDto.getWarehouseId());
            exportOrder.setExpectedDate(exportOrderDto.getExpectedDate());
            exportOrder.setType(exportOrderDto.getType());
            exportOrder.setNote(exportOrderDto.getNote());
            exportOrder.setWarehouseToId(StringUtils.isEmpty(exportOrderDto.getWarehouseToId()) ? null : exportOrderDto.getWarehouseToId());
            exportOrder.setCustomerId(StringUtils.isEmpty(exportOrderDto.getCustomerId()) ? null : exportOrderDto.getCustomerId());

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

        if (exportOrder.getStatus() == ExportOrder.Status.NEW && exportOrderDto.getStatus() == ExportOrder.Status.CANCELED) {
            exportOrder.setStatus(ExportOrder.Status.CANCELED);
            exportOrder.setNote(exportOrderDto.getNote());
            exportOrderRepository.saveAndFlush(exportOrder);

            if (!StringUtils.isEmpty(exportOrder.getId())) {
                PurchaseOrder purchaseOrder = purchaseOrderRepository.findByExportOrderId(exportOrder.getId()).orElse(null);
                if (purchaseOrder != null) {
                    purchaseOrder.setStatus(PurchaseOrder.Status.CANCELED);
                    purchaseOrder.setNote(exportOrderDto.getNote());
                }
            }
            return true;
        }

        if (exportOrder.getStatus() == ExportOrder.Status.CONFIRMED && exportOrderDto.getStatus() == ExportOrder.Status.CANCELED) {
            exportOrder.setStatus(ExportOrder.Status.CANCELED);
            exportOrder.setNote(exportOrderDto.getNote());
            exportOrderRepository.saveAndFlush(exportOrder);

            if (!StringUtils.isEmpty(exportOrder.getId())) {
                PurchaseOrder purchaseOrder = purchaseOrderRepository.findByExportOrderId(exportOrder.getId()).orElse(null);
                if (purchaseOrder != null) {
                    purchaseOrder.setStatus(PurchaseOrder.Status.CANCELED);
                    purchaseOrder.setNote(exportOrderDto.getNote());
                }
            }
            return true;
        }

        if (exportOrder.getStatus() == ExportOrder.Status.NEW && exportOrderDto.getStatus() == ExportOrder.Status.CONFIRMED) {
            exportOrder.setStatus(ExportOrder.Status.CONFIRMED);
            exportOrder.setWarehouseId(exportOrderDto.getWarehouseId());
            exportOrder.setExpectedDate(exportOrderDto.getExpectedDate());
            exportOrder.setType(exportOrderDto.getType());
            exportOrder.setNote(exportOrderDto.getNote());
            exportOrder.setWarehouseToId(StringUtils.isEmpty(exportOrderDto.getWarehouseToId()) ? null : exportOrderDto.getWarehouseToId());
            exportOrder.setCustomerId(StringUtils.isEmpty(exportOrderDto.getCustomerId()) ? null : exportOrderDto.getCustomerId());

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

            List<String> purchaseOrderDetailDeleteIds = exportOrderDto.getDetails().stream()
                    .filter(ExportOrderDetailDto::isDelete)
                    .map(ExportOrderDetailDto::getId)
                    .filter(StringUtils::isNotBlank)
                    .toList();
            exportOrderDetailRepository.deleteByIdIn(purchaseOrderDetailDeleteIds);

            if (exportOrder.getType() == ExportOrder.Type.INTERNAL) {
                PurchaseOrder purchaseOrder = new PurchaseOrder();
                purchaseOrder.setId(generatePurchaseOrderId());
                purchaseOrder.setStatus(PurchaseOrder.Status.CONFIRMED);
                purchaseOrder.setWarehouseId(exportOrder.getWarehouseToId());
                purchaseOrder.setWarehouseFromId(exportOrder.getWarehouseId());
                purchaseOrder.setExpectedDate(exportOrder.getExpectedDate());
                purchaseOrder.setExportOrderId(exportOrder.getId());
                purchaseOrder.setType(PurchaseOrder.Type.INTERNAL);

                List<PurchaseOrderDetail> purchaseOrderDetails = new ArrayList<>();

                Map<String, List<ExportOrderDetailDto>> groupedMap = exportOrderDto.getDetails().stream()
                        .filter(detail -> !detail.isDelete())
                        .collect(Collectors.groupingBy(dto -> dto.getProductId() + "|" + dto.getUnitPrice()));

                for (List<ExportOrderDetailDto> group : groupedMap.values()) {
                    long totalQuantity = 0;
                    BigDecimal totalAmount = BigDecimal.ZERO;
                    ExportOrderDetailDto exportOrderDetailDto = group.get(0);

                    for (ExportOrderDetailDto dto : group) {
                        totalQuantity += dto.getQuantity();
                        totalAmount = totalAmount.add(dto.getTotalAmount());
                    }

                    PurchaseOrderDetail purchaseOrderDetail = new PurchaseOrderDetail();
                    purchaseOrderDetail.setProductId(exportOrderDetailDto.getProductId());
                    purchaseOrderDetail.setQuantity(totalQuantity);
                    purchaseOrderDetail.setUnitPrice(exportOrderDetailDto.getUnitPrice());
                    purchaseOrderDetail.setTotalAmount(totalAmount);
                    purchaseOrderDetail.setStatus(PurchaseOrderDetail.Status.ACTIVE);
                    purchaseOrderDetail.setPurchaseOrderId(purchaseOrder.getId());

                    purchaseOrderDetails.add(purchaseOrderDetail);
                }

                purchaseOrder.setTotalAmount(purchaseOrderDetails.stream().map(PurchaseOrderDetail::getTotalAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));

                purchaseOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);

                purchaseOrderDetailRepository.saveAllAndFlush(purchaseOrderDetails);

                exportOrder.setPurchaseOrderId(purchaseOrder.getId());
                exportOrderRepository.saveAndFlush(exportOrder);
            }
            return true;
        }

        if (exportOrder.getStatus() == ExportOrder.Status.CONFIRMED && exportOrderDto.getStatus() == ExportOrder.Status.DELIVERED) {
            exportOrder.setStatus(ExportOrder.Status.DELIVERED);
            exportOrder.setDeliveredDate(DateUtils.getCurrentTime());

            // 1. Lấy danh sách Product
            List<String> productIds = exportOrderDto.getDetails().stream()
                    .map(ExportOrderDetailDto::getProductId)
                    .toList();
            List<Product> products = productRepository.findByIdIn(productIds);
            Map<String, Product> productMap = products.stream()
                    .collect(Collectors.toMap(Product::getId, p -> p));
            List<Product> productsUpdate = new ArrayList<>();

            // 2. Lấy danh sách Location
            List<String> locationIds = exportOrderDto.getDetails().stream()
                    .map(ExportOrderDetailDto::getLocationId)
                    .toList();
            List<ProductInventoryLocation> productInventoryLocations = productInventoryLocationRepository.findByIdIn(locationIds);
            Map<String, ProductInventoryLocation> locationMap = productInventoryLocations.stream()
                    .collect(Collectors.toMap(ProductInventoryLocation::getId, l -> l));

            // 3. Tạo danh sách ProductInventory để trừ tồn kho sản phẩm
            List<ProductInventory> productInventories = new ArrayList<>();
            List<ProductInventoryLocationHistory> locationHistories = new ArrayList<>();

            for (ExportOrderDetailDto detail : exportOrderDto.getDetails()) {
                // Cập nhật tồn kho tổng của Product
                Product productUpdate = productMap.get(detail.getProductId());
                productUpdate.setInventoryQuantity(
                        productUpdate.getInventoryQuantity() - detail.getQuantity()
                );
                productsUpdate.add(productUpdate);

                // Tạo record ProductInventory (history của xuất hàng)
                ProductInventory productInventory = new ProductInventory();
                productInventory.setType(ProductInventory.Type.EXPORT_ORDER);
                productInventory.setTransactionType(ProductInventory.TransactionType.SUBTRACT);
                productInventory.setQuantity(detail.getQuantity());
                productInventory.setProductId(detail.getProductId());
                productInventory.setExportOrderDetailId(detail.getId());
                productInventories.add(productInventory);

                // Cập nhật tồn kho của Location
                ProductInventoryLocation loc = locationMap.get(detail.getLocationId());
                if (loc != null) {
                    loc.setInventoryQuantity(loc.getInventoryQuantity() - detail.getQuantity());

                    // Tạo history cho location
                    ProductInventoryLocationHistory history = new ProductInventoryLocationHistory();
                    history.setProductInventoryLocationId(loc.getId());
                    history.setExportOrderDetailId(detail.getId());
                    history.setType(ProductInventoryLocationHistory.Type.EXPORT);
                    history.setQuantity(detail.getQuantity());
                    locationHistories.add(history);
                } else {
                    throw new IllegalArgumentException(
                            "Location not found for id: " + detail.getLocationId()
                    );
                }
            }

            // 4. Save lại DB
            productInventoryLocationRepository.saveAllAndFlush(locationMap.values());
            productInventoryLocationHistoryRepository.saveAllAndFlush(locationHistories);
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

    private String generatePurchaseOrderId() {
        return "PO" + "-" + getNextPurchaseOrderSeq()
                + "-W" + "-W" + "-" + new SimpleDateFormat("yyyyMMdd").format(new Date());
    }

    @Override
    public Page<ExportOrder> getExportOrders(DataTableRequest dataTableRequest, String warehouseId, String type, String status) {
        PageRequest pageable = dataTableRequest.toPageable();
        BaseSpecification<ExportOrder> specIdContains = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{ExportOrder.Fields.id})
                        .operation(SearchOperation.CONTAINS)
                        .value(dataTableRequest.getFilter().trim().toUpperCase())
                        .build()
        );
        BaseSpecification<ExportOrder> specCustomerNameContains = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{"customer", Base.Fields.name})
                        .operation(SearchOperation.CONTAINS)
                        .value(dataTableRequest.getFilter().trim().toUpperCase())
                        .build()
        );
        BaseSpecification<ExportOrder> specWarehouseToNameContains = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{"warehouseTo", Base.Fields.name})
                        .operation(SearchOperation.CONTAINS)
                        .value(dataTableRequest.getFilter().trim().toUpperCase())
                        .build()
        );
        BaseSpecification<ExportOrder> specWarehouseIdEqual = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{ExportOrder.Fields.warehouseId})
                        .operation(SearchOperation.EQUALITY)
                        .value(warehouseId)
                        .build()
        );
        BaseSpecification<ExportOrder> specTypeEqual = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{ExportOrder.Fields.type})
                        .operation(SearchOperation.EQUALITY)
                        .value(type)
                        .build()
        );
        BaseSpecification<ExportOrder> specStatusEqual = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{ExportOrder.Fields.status})
                        .operation(SearchOperation.EQUALITY)
                        .value(status)
                        .build()
        );
        return exportOrderRepository.findAll(
                Specification.where(specIdContains)
                        .or(specCustomerNameContains)
                        .or(specWarehouseToNameContains)
                        .and(warehouseId.equals("ALL") ? null : specWarehouseIdEqual)
                        .and(type.equals("ALL") ? null : specTypeEqual)
                        .and(status.equals("ALL") ? null : specStatusEqual)
                , pageable);
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

    @Override
    public Page<ExportOrder> getExportOrders(DataTableRequest dataTableRequest, String customerId) {
        PageRequest pageable = dataTableRequest.toPageable();
        BaseSpecification<ExportOrder> specCustomerIdEqual = new BaseSpecification<>(
                SearchCriteria.builder()
                        .keys(new String[]{ExportOrder.Fields.customerId})
                        .operation(SearchOperation.EQUALITY)
                        .value(customerId)
                        .build()
        );
        return exportOrderRepository.findAll(Specification.where(specCustomerIdEqual), pageable);
    }
}
