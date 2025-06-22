package vubq.warehouse_management.VT_EcoStorage.entities;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import vubq.warehouse_management.VT_EcoStorage.utils.dates.DateUtils;

import java.util.Date;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public abstract class Base {

    private String name;

    private String description;

    private String note;

    private String creator;

    @Column(updatable = false)
    private Date createdAt;

    private String updater;

    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = DateUtils.getCurrentTime();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = DateUtils.getCurrentTime();
    }
}
