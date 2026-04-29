package com.tracy.werewolf.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class BoardDefinition {
    private String id;
    private String name;
    private int playerCount;
    private String description;
    private Map<Role, Integer> roles = new LinkedHashMap<>();

    public BoardDefinition() {}

    public BoardDefinition(String id, String name, int playerCount, String description, Map<Role, Integer> roles) {
        this.id = id;
        this.name = name;
        this.playerCount = playerCount;
        this.description = description;
        this.roles = roles;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getPlayerCount() { return playerCount; }
    public void setPlayerCount(int playerCount) { this.playerCount = playerCount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Map<Role, Integer> getRoles() { return roles; }
    public void setRoles(Map<Role, Integer> roles) { this.roles = roles; }
}
