package vubq.warehouse_management.VT_EcoStorage.controllers;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import vubq.warehouse_management.VT_EcoStorage.dtos.UserDto;
import vubq.warehouse_management.VT_EcoStorage.entities.User;
import vubq.warehouse_management.VT_EcoStorage.services.UserService;
import vubq.warehouse_management.VT_EcoStorage.utils.https.DataTableRequest;
import vubq.warehouse_management.VT_EcoStorage.utils.https.DataTableResponse;
import vubq.warehouse_management.VT_EcoStorage.utils.https.Response;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private final UserService userService;

    @GetMapping("/{userId}")
    public Response getUser(@PathVariable("userId") String userId) {
        return Response.success(userService.getUser(userId));
    }

    @PostMapping("/create-or-update")
    public Response createOrUpdateUser(@RequestBody UserDto userDto) {
        return Response.success(userService.createOrUpdateUser(userDto));
    }

    @GetMapping("/list")
    public Response getListUser(@NonNull DataTableRequest dataTableRequest) {
        Page<User> results = userService.getListUser(dataTableRequest);
        return Response.success(
                DataTableResponse.builder()
                        .list(results.getContent().stream().map(UserDto::toDto).toList())
                        .totalRecords(results.getTotalElements())
                        .build()
        );
    }
}
