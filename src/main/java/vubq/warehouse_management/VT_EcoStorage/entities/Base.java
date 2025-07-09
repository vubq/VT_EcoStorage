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
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import vubq.warehouse_management.VT_EcoStorage.configs.securities.auths.CustomUserDetails;
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

    private String creatorUsername;

    @Column(updatable = false)
    private Date createdAt;

    private String updater;

    private String updaterUsername;

    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = DateUtils.getCurrentTime();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null) {
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            creatorUsername = customUserDetails.getUser().getUsername();
            creator = customUserDetails.getUser().getId();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = DateUtils.getCurrentTime();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null) {
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            updaterUsername = customUserDetails.getUser().getUsername();
            updater = customUserDetails.getUser().getId();
        }
    }
}
