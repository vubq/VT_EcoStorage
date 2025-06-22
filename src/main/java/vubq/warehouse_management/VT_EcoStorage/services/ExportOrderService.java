package vubq.warehouse_management.VT_EcoStorage.services;

import org.springframework.data.domain.Page;
import vubq.warehouse_management.VT_EcoStorage.dtos.ExportOrderDto;
import vubq.warehouse_management.VT_EcoStorage.entities.ExportOrder;
import vubq.warehouse_management.VT_EcoStorage.utils.https.DataTableRequest;

public interface ExportOrderService {
    boolean createOrUpdateExportOrder(ExportOrderDto exportOrderDto);

    Page<ExportOrder> getExportOrders(DataTableRequest dataTableRequest);
}
