package com.tracy.werewolf.model;
public class RoleInfo {
    private String id;
    private String name;
    private RoleTeam team;
    private String description;
    private String boardConfig;
    private String recommendedPlayers;

    public RoleInfo() {}

    public RoleInfo(String id, String name, RoleTeam team, String description, String boardConfig, String recommendedPlayers) {
        this.id = id;
        this.name = name;
        this.team = team;
        this.description = description;
        this.boardConfig = boardConfig;
        this.recommendedPlayers = recommendedPlayers;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public RoleTeam getTeam() { return team; }
    public void setTeam(RoleTeam team) { this.team = team; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getBoardConfig() { return boardConfig; }
    public void setBoardConfig(String boardConfig) { this.boardConfig = boardConfig; }
    public String getRecommendedPlayers() { return recommendedPlayers; }
    public void setRecommendedPlayers(String recommendedPlayers) { this.recommendedPlayers = recommendedPlayers; }
}
