package com.tracy.werewolf.dto;

import com.tracy.werewolf.model.*;
import java.util.*;

public class GameRoomResponse {
    private String roomCode;
    private int playerCount;
    private String boardId;
    private boolean customMode;
    private Map<String, Integer> customRoles;
    private GamePhase phase;
    private int round;
    private String hostPlayerId;
    private List<PlayerResponse> players;

    private NightAction currentNightAction;
    private long nightActionEndsAtEpochMs;
    private Integer wolfKillTargetSeatNumber;
    private String wolfKillActorPlayerId;
    private boolean witchSavedWolfKill;
    private Integer witchPoisonTargetSeatNumber;
    private List<Integer> nightDeathSeatNumbers;
    private String nightDeathMessage;
    private List<Integer> hunterCanShootSeatNumbers;
    private Integer mechanicalWolfLearnedSeatNumber;
    private String mechanicalWolfLearnedRole;
    private String mechanicalWolfLearnedRoleName;

    public GameRoomResponse(GameRoom room) {
        this.roomCode = room.getRoomCode();
        this.playerCount = room.getPlayerCount();
        this.boardId = room.getBoardId();
        this.customMode = room.isCustomMode();
        this.customRoles = room.getCustomRoles();
        this.phase = room.getPhase();
        this.round = room.getRound();
        this.hostPlayerId = room.getHostPlayerId();
        this.players = room.getPlayers().stream()
                .map(p -> new PlayerResponse(p.getId(), p.getName(), p.isAlive(), p.getSeatNumber(), p.isHost()))
                .toList();
        this.currentNightAction = room.getCurrentNightAction();
        this.nightActionEndsAtEpochMs = room.getNightActionEndsAtEpochMs();
        this.wolfKillTargetSeatNumber = room.getWolfKillTargetSeatNumber();
        this.wolfKillActorPlayerId = room.getWolfKillActorPlayerId();
        this.witchSavedWolfKill = room.isWitchSavedWolfKill();
        this.witchPoisonTargetSeatNumber = room.getWitchPoisonTargetSeatNumber();
        this.nightDeathSeatNumbers = room.getNightDeathSeatNumbers();
        this.nightDeathMessage = room.getNightDeathMessage();
        this.hunterCanShootSeatNumbers = room.getHunterCanShootSeatNumbers();
        this.mechanicalWolfLearnedSeatNumber = room.getMechanicalWolfLearnedSeatNumber();
        this.mechanicalWolfLearnedRole = room.getMechanicalWolfLearnedRole();
        this.mechanicalWolfLearnedRoleName = room.getMechanicalWolfLearnedRoleName();
    }

    public String getRoomCode() { return roomCode; }
    public int getPlayerCount() { return playerCount; }
    public String getBoardId() { return boardId; }
    public boolean isCustomMode() { return customMode; }
    public Map<String, Integer> getCustomRoles() { return customRoles; }
    public GamePhase getPhase() { return phase; }
    public int getRound() { return round; }
    public String getHostPlayerId() { return hostPlayerId; }
    public List<PlayerResponse> getPlayers() { return players; }
    public NightAction getCurrentNightAction() { return currentNightAction; }
    public long getNightActionEndsAtEpochMs() { return nightActionEndsAtEpochMs; }
    public Integer getWolfKillTargetSeatNumber() { return wolfKillTargetSeatNumber; }
    public String getWolfKillActorPlayerId() { return wolfKillActorPlayerId; }
    public boolean isWitchSavedWolfKill() { return witchSavedWolfKill; }
    public Integer getWitchPoisonTargetSeatNumber() { return witchPoisonTargetSeatNumber; }
    public List<Integer> getNightDeathSeatNumbers() { return nightDeathSeatNumbers; }
    public String getNightDeathMessage() { return nightDeathMessage; }
    public List<Integer> getHunterCanShootSeatNumbers() { return hunterCanShootSeatNumbers; }
    public Integer getMechanicalWolfLearnedSeatNumber() { return mechanicalWolfLearnedSeatNumber; }
    public String getMechanicalWolfLearnedRole() { return mechanicalWolfLearnedRole; }
    public String getMechanicalWolfLearnedRoleName() { return mechanicalWolfLearnedRoleName; }
}
