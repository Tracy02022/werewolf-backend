package com.tracy.werewolf.model;
import java.util.*;
public class GameRoom {
    private String roomCode;
    private int playerCount;
    private String boardId;
    private boolean customMode;
    private Map<String, Integer> customRoles;
    private GamePhase phase = GamePhase.WAITING;
    private int round = 0;
    private String hostPlayerId;
    private List<Player> players = new ArrayList<>();

    public String getRoomCode() { return roomCode; }
    public void setRoomCode(String roomCode) { this.roomCode = roomCode; }
    public int getPlayerCount() { return playerCount; }
    public void setPlayerCount(int playerCount) { this.playerCount = playerCount; }
    public String getBoardId() { return boardId; }
    public void setBoardId(String boardId) { this.boardId = boardId; }
    public boolean isCustomMode() { return customMode; }
    public void setCustomMode(boolean customMode) { this.customMode = customMode; }
    public Map<String, Integer> getCustomRoles() { return customRoles; }
    public void setCustomRoles(Map<String, Integer> customRoles) { this.customRoles = customRoles; }
    public GamePhase getPhase() { return phase; }
    public void setPhase(GamePhase phase) { this.phase = phase; }
    public int getRound() { return round; }
    public void setRound(int round) { this.round = round; }
    public String getHostPlayerId() { return hostPlayerId; }
    public void setHostPlayerId(String hostPlayerId) { this.hostPlayerId = hostPlayerId; }
    public List<Player> getPlayers() { return players; }
    public void setPlayers(List<Player> players) { this.players = players; }
}
