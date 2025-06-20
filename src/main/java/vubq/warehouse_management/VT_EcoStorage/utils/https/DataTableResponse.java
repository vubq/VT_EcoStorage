package vubq.warehouse_management.VT_EcoStorage.utils.https;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataTableResponse {

    private Object list;
    private Long totalRecords;
}
