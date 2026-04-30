package com.tracy.werewolf.model;
import java.util.Map;
public class Board {
    private String id;
    private String name;
    private int playerCount;
    private Map<String, Integer> roles;
    private String description;

    public Board() {}

    public Board(String id, String name, int playerCount, Map<String, Integer> roles, String description) {
        this.id = id;
        this.name = name;
        this.playerCount = playerCount;
        this.roles = roles;
        this.description = description;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getPlayerCount() { return playerCount; }
    public void setPlayerCount(int playerCount) { this.playerCount = playerCount; }
    public Map<String, Integer> getRoles() { return roles; }
    public void setRoles(Map<String, Integer> roles) { this.roles = roles; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
