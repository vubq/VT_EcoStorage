package vubq.warehouse_management.VT_EcoStorage.utils.specifications;

import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serial;

@AllArgsConstructor
public class BaseSpecification<T> implements Specification<T> {

    @Serial
    private static final long serialVersionUID = 1L;

    private SearchCriteria criteria;

    @Override
    public Predicate toPredicate(@NonNull Root<T> root, CriteriaQuery<?> query, @NonNull CriteriaBuilder builder) {
        if (criteria == null || criteria.getOperation() == null) {
            return null;
        }

        Path<?> path = root;
        for (String key : criteria.getKeys()) {
            path = path.get(key);
        }

        Object value = criteria.getValue();

        switch (criteria.getOperation()) {
            case EQUALITY:
                return hasValue(value) ? builder.equal(path, value) : null;

            case NEGATION:
                return builder.notEqual(path, value);

            case GREATER_THAN:
                return builder.greaterThan(path.as(String.class), value.toString());

            case GREATER_THAN_OR_EQUAL:
                return builder.greaterThanOrEqualTo(path.as(String.class), value.toString());

            case LESS_THAN:
                return builder.lessThan(path.as(String.class), value.toString());

            case LESS_THAN_OR_EQUAL:
                return builder.lessThanOrEqualTo(path.as(String.class), value.toString());

            case LIKE:
                return hasValue(value) ? builder.like(path.as(String.class), value.toString()) : null;

            case STARTS_WITH:
                return hasValue(value) ? builder.like(path.as(String.class), value + "%") : null;

            case ENDS_WITH:
                return hasValue(value) ? builder.like(path.as(String.class), "%" + value) : null;

            case CONTAINS:
                return hasValue(value)
                        ? builder.like(builder.upper(path.as(String.class)), "%" + value.toString().toUpperCase() + "%")
                        : null;

            case IN:
                return criteria.getListValue() != null && !criteria.getListValue().isEmpty()
                        ? path.in(criteria.getListValue())
                        : null;

            default:
                return null;
        }
    }

    private boolean hasValue(Object value) {
        return value instanceof String ? StringUtils.isNotBlank((String) value) : value != null;
    }
}
