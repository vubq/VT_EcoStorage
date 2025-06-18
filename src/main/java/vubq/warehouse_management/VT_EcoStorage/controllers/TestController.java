package vubq.warehouse_management.VT_EcoStorage.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vubq.warehouse_management.VT_EcoStorage.utils.https.Response;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    @PreAuthorize("hasAuthority('NHAPHANG_THEM1') or hasAuthority('NHAPHANG_THEM2')")
    @GetMapping
    public Response get() {
        return Response.success();
    }
}
