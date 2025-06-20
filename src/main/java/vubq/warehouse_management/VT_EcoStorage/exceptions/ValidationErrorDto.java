package vubq.warehouse_management.VT_EcoStorage.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ValidationErrorDto {
    private String field;
    private List<String> messages;
}
