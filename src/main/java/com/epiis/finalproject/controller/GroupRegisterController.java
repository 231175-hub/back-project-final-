package com.epiis.finalproject.controller;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;

import com.epiis.finalproject.business.BusinessGroupRegister;
import com.epiis.finalproject.dto.request.groupregister.RequestGroupRegisterSave;
import com.epiis.finalproject.dto.response.groupregister.ResponseGroupRegisterData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
@RestController
@RequestMapping("/intranet")
public class GroupRegisterController {

    private final BusinessGroupRegister businessGroupRegister;

    public GroupRegisterController(BusinessGroupRegister businessGroupRegister) {
        this.businessGroupRegister = businessGroupRegister;
    }

    @GetMapping("/group-register/{idGroup}")
    public ResponseEntity<ResponseGroupRegisterData> getGroupRegisterData(@PathVariable String idGroup) {
        ResponseGroupRegisterData response = businessGroupRegister.getGroupRegisterData(idGroup);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/group-register/{idGroup}")
    public ResponseEntity<Map<String, Object>> saveGroupRegisterData(
            @PathVariable String idGroup,
            @Valid @RequestBody RequestGroupRegisterSave request) {
        
        Map<String, Object> response = businessGroupRegister.saveGroupRegisterData(idGroup, request);
        if (response.containsKey("success") && (Boolean) response.get("success")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/group-register/{idGroup}/close")
    public ResponseEntity<Map<String, Object>> closeGroupRegister(@PathVariable String idGroup) {
        Map<String, Object> response = businessGroupRegister.closeGroupRegister(idGroup);
        if (response.containsKey("success") && (Boolean) response.get("success")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
