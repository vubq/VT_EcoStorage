package vubq.warehouse_management.VT_EcoStorage.configs.securities.auths;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import vubq.warehouse_management.VT_EcoStorage.entities.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<String> permissionIds = new HashSet<>();

        if (user.getUserPermissions() != null) {
            for (UserPermission up : user.getUserPermissions()) {
                SystemPermission permission = up.getSystemPermission();
                if (permission != null) {
                    permissionIds.add(permission.getId());
                }
            }
        }

        if (user.getUserPermissionGroups() != null) {
            for (UserPermissionGroup groupLink : user.getUserPermissionGroups()) {
                SystemPermissionGroup group = groupLink.getSystemPermissionGroup();
                if (group != null && group.getGroupDetails() != null) {
                    for (SystemPermissionGroupDetail detail : group.getGroupDetails()) {
                        SystemPermission permission = detail.getSystemPermission();
                        if (permission != null) {
                            permissionIds.add(permission.getId());
                        }
                    }
                }
            }
        }

        return permissionIds.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

