package vubq.warehouse_management.VT_EcoStorage.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vubq.warehouse_management.VT_EcoStorage.entities.ExportOrder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExportOrderDto {
    private String id;
    private ExportOrder.Status status;
    private Date expectedDate;
    private Date deliveredDate;
    private BigDecimal totalAmount;
    private ExportOrder.Type type;
    private String customerId;
    private List<ExportOrderDetailDto> details;
}
