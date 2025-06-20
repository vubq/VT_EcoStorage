package vubq.warehouse_management.VT_EcoStorage.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vubq.warehouse_management.VT_EcoStorage.configs.securities.auths.CustomUserDetails;
import vubq.warehouse_management.VT_EcoStorage.configs.securities.auths.JwtUtil;
import vubq.warehouse_management.VT_EcoStorage.dtos.requests.AuthRequest;
import vubq.warehouse_management.VT_EcoStorage.dtos.responses.AuthResponse;
import vubq.warehouse_management.VT_EcoStorage.utils.https.Response;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public Response login(@RequestBody AuthRequest request) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(request.getUsername());
            List<String> permissions = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
            String token = jwtUtil.generateToken(request.getUsername());
            return Response.success(new AuthResponse(userDetails.getUser().getId(), permissions, token, ""));
        } catch (AuthenticationException e) {
            log.error(e.getMessage());
            return Response.unauthorized("Invalid username or password");
        }
    }
}