package vubq.warehouse_management.VT_EcoStorage.utils.specifications;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria {

    private String[] keys;

    private SearchOperation operation;

    private Object value;

    private List<?> listValue;

}
