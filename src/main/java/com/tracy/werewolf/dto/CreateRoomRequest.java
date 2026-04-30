package com.tracy.werewolf.dto;
import java.util.Map;
public class CreateRoomRequest {
    private int playerCount;
    private String hostName;
    private String boardId;
    private boolean customMode;
    private Map<String, Integer> customRoles;

    public int getPlayerCount() { return playerCount; }
    public void setPlayerCount(int playerCount) { this.playerCount = playerCount; }
    public String getHostName() { return hostName; }
    public void setHostName(String hostName) { this.hostName = hostName; }
    public String getBoardId() { return boardId; }
    public void setBoardId(String boardId) { this.boardId = boardId; }
    public boolean isCustomMode() { return customMode; }
    public void setCustomMode(boolean customMode) { this.customMode = customMode; }
    public Map<String, Integer> getCustomRoles() { return customRoles; }
    public void setCustomRoles(Map<String, Integer> customRoles) { this.customRoles = customRoles; }
}
