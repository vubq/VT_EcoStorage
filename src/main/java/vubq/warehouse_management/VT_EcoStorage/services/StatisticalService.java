package vubq.warehouse_management.VT_EcoStorage.services;

import vubq.warehouse_management.VT_EcoStorage.dtos.ProductStatisticalDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.responses.ReferenceDataStatisticalResponse;

import java.util.Date;
import java.util.List;

public interface StatisticalService {

    List<ProductStatisticalDto> getStatistical(Date startDate, Date endDate, String warehouseId, String keyword, boolean onlyWithTransaction);

    ReferenceDataStatisticalResponse getReferenceDataStatistical();
}
