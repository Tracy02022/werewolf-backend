package com.tracy.werewolf.dto;
import com.tracy.werewolf.model.RoleInfo;
public class RoleResponse {
    private String role;
    private RoleInfo roleInfo;

    public RoleResponse(String role, RoleInfo roleInfo) {
        this.role = role;
        this.roleInfo = roleInfo;
    }

    public String getRole() { return role; }
    public RoleInfo getRoleInfo() { return roleInfo; }
}
