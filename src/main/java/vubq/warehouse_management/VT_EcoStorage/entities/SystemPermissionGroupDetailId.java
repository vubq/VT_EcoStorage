package vubq.warehouse_management.VT_EcoStorage.entities;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SystemPermissionGroupDetailId implements Serializable {
    private String systemPermissionGroupId;
    private String systemPermissionId;
}
