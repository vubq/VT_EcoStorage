package vubq.warehouse_management.VT_EcoStorage.controllers;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vubq.warehouse_management.VT_EcoStorage.dtos.FloorDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.ShelfDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.WarehouseDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.ZoneDto;
import vubq.warehouse_management.VT_EcoStorage.dtos.requests.MoveLocationRequest;
import vubq.warehouse_management.VT_EcoStorage.entities.Warehouse;
import vubq.warehouse_management.VT_EcoStorage.services.WarehouseService;
import vubq.warehouse_management.VT_EcoStorage.utils.https.Response;

import java.util.Collection;

@RestController
@RequestMapping("/api/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    final private WarehouseService warehouseService;

    @PreAuthorize("hasAuthority('ADMIN.SUPER') or hasAuthority('WAREHOUSE.VIEW')")
    @GetMapping("/list")
    public Response getListWarehouse() {
        return Response.success(warehouseService.getWarehouses());
    }

    @GetMapping("/{warehouseId}")
    public Response getWarehouse(@PathVariable("warehouseId") String warehouseId) {
        return Response.success(warehouseService.getWarehouse(warehouseId));
    }

    @PostMapping("/create-or-update-warehouse")
    public Response createOrUpdateWarehouse(@RequestBody WarehouseDto warehouseDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        boolean hasEdit = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("WAREHOUSE.EDIT"));
        boolean hasAdd = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("WAREHOUSE.ADD"));
        boolean isSuperAdmin = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN.SUPER"));

        if(!isSuperAdmin) {
            if (!StringUtils.isEmpty(warehouseDto.getId())) {
                if (!hasEdit) {
                    throw new AccessDeniedException("Không có quyền");
                }
            } else {
                if (!hasAdd) {
                    throw new AccessDeniedException("Không có quyền");
                }
            }
        }
        return Response.success(warehouseService.createOrUpdateWarehouse(warehouseDto));
    }

    @PostMapping("/create-or-update-zone")
    public Response createOrUpdateZone(@RequestBody ZoneDto zoneDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        boolean hasEdit = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("WAREHOUSE.EDIT"));
        boolean hasAdd = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("WAREHOUSE.ADD"));
        boolean isSuperAdmin = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN.SUPER"));

        if(!isSuperAdmin) {
            if (!StringUtils.isEmpty(zoneDto.getId())) {
                if (!hasEdit) {
                    throw new AccessDeniedException("Không có quyền");
                }
            } else {
                if (!hasAdd) {
                    throw new AccessDeniedException("Không có quyền");
                }
            }
        }
        return Response.success(warehouseService.createOrUpdateZone(zoneDto));
    }

    @PostMapping("/create-or-update-shelf")
    public Response createOrUpdateShelf(@RequestBody ShelfDto shelfDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        boolean hasEdit = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("WAREHOUSE.EDIT"));
        boolean hasAdd = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("WAREHOUSE.ADD"));
        boolean isSuperAdmin = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN.SUPER"));

        if(!isSuperAdmin) {
            if (!StringUtils.isEmpty(shelfDto.getId())) {
                if (!hasEdit) {
                    throw new AccessDeniedException("Không có quyền");
                }
            } else {
                if (!hasAdd) {
                    throw new AccessDeniedException("Không có quyền");
                }
            }
        }
        return Response.success(warehouseService.createOrUpdateShelf(shelfDto));
    }

    @PostMapping("/create-or-update-floor")
    public Response createOrUpdateFloor(@RequestBody FloorDto floorDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        boolean hasEdit = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("WAREHOUSE.EDIT"));
        boolean hasAdd = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("WAREHOUSE.ADD"));
        boolean isSuperAdmin = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN.SUPER"));

        if(!isSuperAdmin) {
            if (!StringUtils.isEmpty(floorDto.getId())) {
                if (!hasEdit) {
                    throw new AccessDeniedException("Không có quyền");
                }
            } else {
                if (!hasAdd) {
                    throw new AccessDeniedException("Không có quyền");
                }
            }
        }
        return Response.success(warehouseService.createOrUpdateFloor(floorDto));
    }

    @PreAuthorize("hasAuthority('ADMIN.SUPER') or hasAuthority('WAREHOUSE.EDIT')")
    @PostMapping("/move-location")
    public Response moveLocation(@RequestBody MoveLocationRequest moveLocationRequest) {
        boolean success = warehouseService.moveLocation(moveLocationRequest);
        return Response.success(success);
    }
}
