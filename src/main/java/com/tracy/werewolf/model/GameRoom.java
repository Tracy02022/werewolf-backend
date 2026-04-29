package com.tracy.werewolf.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GameRoom {
    private String roomCode;
    private int playerCount;
    private GamePhase phase = GamePhase.WAITING;
    private int round = 0;
    private String hostPlayerId;
    private String boardId;
    private String boardName;
    private boolean customMode;
    private Map<Role, Integer> customRoles = new LinkedHashMap<>();
    private List<Player> players = new ArrayList<>();

    public String getRoomCode() { return roomCode; }
    public void setRoomCode(String roomCode) { this.roomCode = roomCode; }
    public int getPlayerCount() { return playerCount; }
    public void setPlayerCount(int playerCount) { this.playerCount = playerCount; }
    public GamePhase getPhase() { return phase; }
    public void setPhase(GamePhase phase) { this.phase = phase; }
    public int getRound() { return round; }
    public void setRound(int round) { this.round = round; }
    public String getHostPlayerId() { return hostPlayerId; }
    public void setHostPlayerId(String hostPlayerId) { this.hostPlayerId = hostPlayerId; }
    public String getBoardId() { return boardId; }
    public void setBoardId(String boardId) { this.boardId = boardId; }
    public String getBoardName() { return boardName; }
    public void setBoardName(String boardName) { this.boardName = boardName; }
    public boolean isCustomMode() { return customMode; }
    public void setCustomMode(boolean customMode) { this.customMode = customMode; }
    public Map<Role, Integer> getCustomRoles() { return customRoles; }
    public void setCustomRoles(Map<Role, Integer> customRoles) { this.customRoles = customRoles; }
    public List<Player> getPlayers() { return players; }
    public void setPlayers(List<Player> players) { this.players = players; }
}
