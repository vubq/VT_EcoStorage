package vubq.warehouse_management.VT_EcoStorage.entities;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class SystemPermissionGroupDetailId implements Serializable {
    private String systemPermissionGroupId;
    private String systemPermissionId;
}
