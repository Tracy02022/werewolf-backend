package com.tracy.werewolf.controller;
import com.tracy.werewolf.model.RoleInfo;
import com.tracy.werewolf.service.RoleCatalogService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*")
public class RoleController {
    private final RoleCatalogService roleCatalogService;

    public RoleController(RoleCatalogService roleCatalogService) {
        this.roleCatalogService = roleCatalogService;
    }

    @GetMapping
    public List<RoleInfo> getRoles(@RequestParam(required = false) String team) {
        return roleCatalogService.getRoles(team);
    }
}
