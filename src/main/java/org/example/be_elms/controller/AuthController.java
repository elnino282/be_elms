package org.example.be_elms.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.be_elms.dto.ApiResponse;
import org.example.be_elms.dto.LoginRequest;
import org.example.be_elms.dto.LoginResponse;
import org.example.be_elms.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }
}

