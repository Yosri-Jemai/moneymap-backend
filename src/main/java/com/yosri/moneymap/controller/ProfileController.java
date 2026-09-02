package com.yosri.moneymap.controller;

import com.yosri.moneymap.dto.AuthDTO;
import com.yosri.moneymap.dto.ProfileDTO;
import com.yosri.moneymap.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<ProfileDTO> register(@RequestBody ProfileDTO profileDTO) {
        ProfileDTO registeredProfile = profileService.registerProfile(profileDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredProfile);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String token) {
        boolean isActivated = profileService.activateAccount(token);
        if (isActivated) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Profile Activated");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile Activation Failed");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthDTO AuthDTO) {
        try {
            if (!profileService.isAccountActive(AuthDTO.getEmail())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Account Not Active"));
            }
            Map<String,Object> response = profileService.authenticateAndGenerateToken(AuthDTO);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }
}
