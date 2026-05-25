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

    private NightAction currentNightAction = NightAction.NONE;
    private long nightActionEndsAtEpochMs;

    private Integer wolfKillTargetSeatNumber;
    private String wolfKillActorPlayerId;
    private boolean witchSavedWolfKill;
    private Integer witchPoisonTargetSeatNumber;
    private boolean witchSaveUsed;
    private boolean witchPoisonUsed;
    private List<Integer> nightDeathSeatNumbers = new ArrayList<>();
    private String nightDeathMessage = "";
    private boolean firstDayNightReportReleased = false;
    private List<Integer> hunterCanShootSeatNumbers = new ArrayList<>();

    private Integer seerCheckedSeatNumber;
    private String seerCheckedTeam;
    private String seerCheckedRole;
    private String seerCheckedRoleName;

    private Integer mechanicalWolfLearnedSeatNumber;
    private String mechanicalWolfLearnedRole;
    private String mechanicalWolfLearnedRoleName;

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

    public NightAction getCurrentNightAction() { return currentNightAction; }
    public void setCurrentNightAction(NightAction currentNightAction) { this.currentNightAction = currentNightAction; }
    public long getNightActionEndsAtEpochMs() { return nightActionEndsAtEpochMs; }
    public void setNightActionEndsAtEpochMs(long nightActionEndsAtEpochMs) { this.nightActionEndsAtEpochMs = nightActionEndsAtEpochMs; }

    public Integer getWolfKillTargetSeatNumber() { return wolfKillTargetSeatNumber; }
    public void setWolfKillTargetSeatNumber(Integer wolfKillTargetSeatNumber) { this.wolfKillTargetSeatNumber = wolfKillTargetSeatNumber; }
    public String getWolfKillActorPlayerId() { return wolfKillActorPlayerId; }
    public void setWolfKillActorPlayerId(String wolfKillActorPlayerId) { this.wolfKillActorPlayerId = wolfKillActorPlayerId; }
    public boolean isWitchSavedWolfKill() { return witchSavedWolfKill; }
    public void setWitchSavedWolfKill(boolean witchSavedWolfKill) { this.witchSavedWolfKill = witchSavedWolfKill; }
    public Integer getWitchPoisonTargetSeatNumber() { return witchPoisonTargetSeatNumber; }
    public void setWitchPoisonTargetSeatNumber(Integer witchPoisonTargetSeatNumber) { this.witchPoisonTargetSeatNumber = witchPoisonTargetSeatNumber; }
    public boolean isWitchSaveUsed() { return witchSaveUsed; }
    public void setWitchSaveUsed(boolean witchSaveUsed) { this.witchSaveUsed = witchSaveUsed; }
    public boolean isWitchPoisonUsed() { return witchPoisonUsed; }
    public void setWitchPoisonUsed(boolean witchPoisonUsed) { this.witchPoisonUsed = witchPoisonUsed; }
    public List<Integer> getNightDeathSeatNumbers() { return nightDeathSeatNumbers; }
    public void setNightDeathSeatNumbers(List<Integer> nightDeathSeatNumbers) { this.nightDeathSeatNumbers = nightDeathSeatNumbers; }
    public String getNightDeathMessage() { return nightDeathMessage; }
    public void setNightDeathMessage(String nightDeathMessage) { this.nightDeathMessage = nightDeathMessage; }
    public boolean isFirstDayNightReportReleased() { return firstDayNightReportReleased; }
    public void setFirstDayNightReportReleased(boolean firstDayNightReportReleased) { this.firstDayNightReportReleased = firstDayNightReportReleased; }
    public List<Integer> getHunterCanShootSeatNumbers() { return hunterCanShootSeatNumbers; }
    public void setHunterCanShootSeatNumbers(List<Integer> hunterCanShootSeatNumbers) { this.hunterCanShootSeatNumbers = hunterCanShootSeatNumbers; }

    public Integer getSeerCheckedSeatNumber() { return seerCheckedSeatNumber; }
    public void setSeerCheckedSeatNumber(Integer seerCheckedSeatNumber) { this.seerCheckedSeatNumber = seerCheckedSeatNumber; }
    public String getSeerCheckedTeam() { return seerCheckedTeam; }
    public void setSeerCheckedTeam(String seerCheckedTeam) { this.seerCheckedTeam = seerCheckedTeam; }
    public String getSeerCheckedRole() { return seerCheckedRole; }
    public void setSeerCheckedRole(String seerCheckedRole) { this.seerCheckedRole = seerCheckedRole; }
    public String getSeerCheckedRoleName() { return seerCheckedRoleName; }
    public void setSeerCheckedRoleName(String seerCheckedRoleName) { this.seerCheckedRoleName = seerCheckedRoleName; }

    public Integer getMechanicalWolfLearnedSeatNumber() { return mechanicalWolfLearnedSeatNumber; }
    public void setMechanicalWolfLearnedSeatNumber(Integer mechanicalWolfLearnedSeatNumber) { this.mechanicalWolfLearnedSeatNumber = mechanicalWolfLearnedSeatNumber; }
    public String getMechanicalWolfLearnedRole() { return mechanicalWolfLearnedRole; }
    public void setMechanicalWolfLearnedRole(String mechanicalWolfLearnedRole) { this.mechanicalWolfLearnedRole = mechanicalWolfLearnedRole; }
    public String getMechanicalWolfLearnedRoleName() { return mechanicalWolfLearnedRoleName; }
    public void setMechanicalWolfLearnedRoleName(String mechanicalWolfLearnedRoleName) { this.mechanicalWolfLearnedRoleName = mechanicalWolfLearnedRoleName; }
}
