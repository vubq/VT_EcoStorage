package vubq.warehouse_management.VT_EcoStorage.utils.https;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Getter
@Setter
public class DataTableRequest {
    Integer currentPage;

    Integer perPage;

    String filter;

    String sortBy;

    Boolean sortDesc;

    public PageRequest toPageable() {
        currentPage = currentPage != null && currentPage > 0 ? currentPage : 1;
        perPage = perPage != null && perPage > 0 ? perPage : 10;

        PageRequest pageable = null;
        if (StringUtils.isEmpty(this.getSortBy())) {
            pageable = PageRequest.of(this.getCurrentPage() - 1, this.getPerPage());
        } else {
            pageable = PageRequest.of(
                    this.getCurrentPage() - 1,
                    this.getPerPage(),
                    Sort.by(this.getSortDesc() ? Sort.Direction.DESC : Sort.Direction.ASC, this.getSortBy())
            );
        }
        return pageable;
    }
}

