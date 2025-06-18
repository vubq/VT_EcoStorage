package vubq.warehouse_management.VT_EcoStorage.entities;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class UserPermissionGroupId implements Serializable {
    private String userId;
    private String systemPermissionGroupId;
}
