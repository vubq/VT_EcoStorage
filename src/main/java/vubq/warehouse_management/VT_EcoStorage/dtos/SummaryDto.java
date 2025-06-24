package vubq.warehouse_management.VT_EcoStorage.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SummaryDto {
    private String productId;
    private Long importQuantity;
    private BigDecimal importTotal;
    private Long exportQuantity;
    private BigDecimal exportTotal;
    private BigDecimal revenue;
}
