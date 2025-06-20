package vubq.warehouse_management.VT_EcoStorage.entities;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vubq.warehouse_management.VT_EcoStorage.utils.dates.DateUtils;

import java.util.Date;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Base {

    private String creator;

    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    private String updater;

    @Column(name = "updated_at")
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
