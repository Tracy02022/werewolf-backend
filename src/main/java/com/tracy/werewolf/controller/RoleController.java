package com.tracy.werewolf.controller;

import com.tracy.werewolf.dto.RoleInfo;
import com.tracy.werewolf.model.Role;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*")
public class RoleController {
    @GetMapping
    public List<RoleInfo> getRoles() {
        return Arrays.stream(Role.values())
                .map(role -> new RoleInfo(role, role.getDisplayName(), role.getCategory()))
                .toList();
    }
}
