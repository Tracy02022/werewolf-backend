package com.tracy.werewolf.dto;

import com.tracy.werewolf.model.Role;
import com.tracy.werewolf.model.RoleCategory;

public class RoleInfo {
    private Role id;
    private String name;
    private RoleCategory category;

    public RoleInfo(Role id, String name, RoleCategory category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }

    public Role getId() { return id; }
    public void setId(Role id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public RoleCategory getCategory() { return category; }
    public void setCategory(RoleCategory category) { this.category = category; }
}
