package vubq.warehouse_management.VT_EcoStorage.services;

import org.springframework.data.domain.Page;
import vubq.warehouse_management.VT_EcoStorage.dtos.PurchaseOrderDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.responses.ReferenceDataPurchaseOrderResponse;
import vubq.warehouse_management.VT_EcoStorage.entities.PurchaseOrder;
import vubq.warehouse_management.VT_EcoStorage.utils.https.DataTableRequest;

public interface PurchaseOrderService {

    boolean createOrUpdatePurchaseOrder(PurchaseOrderDto purchaseOrderDto);

    Page<PurchaseOrder> getListPurchaseOrder(DataTableRequest dataTableRequest);

    ReferenceDataPurchaseOrderResponse getReferenceDataPurchaseOrder();

    PurchaseOrderDto getPurchaseOrder(String purchaseOrderId);

    Page<PurchaseOrder> getPurchaseOrders(DataTableRequest dataTableRequest, String supplierId);
}
