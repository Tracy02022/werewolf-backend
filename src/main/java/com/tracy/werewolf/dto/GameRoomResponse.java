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
    private boolean nightActionCompleted;
    private Integer guardTargetSeatNumber;
    private Integer previousGuardTargetSeatNumber;
    private Integer wolfKillTargetSeatNumber;
    private String wolfKillActorPlayerId;
    private boolean witchSavedWolfKill;
    private Integer witchPoisonTargetSeatNumber;
    private boolean witchSaveUsed;
    private boolean witchPoisonUsed;
    private List<Integer> nightDeathSeatNumbers;
    private String nightDeathMessage;
    private boolean firstDayNightReportReleased;
    private List<Integer> hunterCanShootSeatNumbers;
    private Integer seerCheckedSeatNumber;
    private String seerCheckedTeam;
    private String seerCheckedRole;
    private String seerCheckedRoleName;
    private Integer mechanicalWolfLearnedSeatNumber;
    private String mechanicalWolfLearnedRole;
    private String mechanicalWolfLearnedRoleName;
    private Integer mechanicalWolfSkillTargetSeatNumber;
    private String mechanicalWolfSkillResult;
    private boolean mechanicalWolfPoisonUsed;
    private boolean mechanicalWolfCanJoinWolfKill;

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
        this.nightActionCompleted = room.isNightActionCompleted();
        this.guardTargetSeatNumber = room.getGuardTargetSeatNumber();
        this.previousGuardTargetSeatNumber = room.getPreviousGuardTargetSeatNumber();
        this.wolfKillTargetSeatNumber = room.getWolfKillTargetSeatNumber();
        this.wolfKillActorPlayerId = room.getWolfKillActorPlayerId();
        this.witchSavedWolfKill = room.isWitchSavedWolfKill();
        this.witchPoisonTargetSeatNumber = room.getWitchPoisonTargetSeatNumber();
        this.witchSaveUsed = room.isWitchSaveUsed();
        this.witchPoisonUsed = room.isWitchPoisonUsed();
        this.nightDeathSeatNumbers = room.getNightDeathSeatNumbers();
        this.nightDeathMessage = room.getNightDeathMessage();
        this.firstDayNightReportReleased = room.isFirstDayNightReportReleased();
        this.hunterCanShootSeatNumbers = room.getHunterCanShootSeatNumbers();
        this.seerCheckedSeatNumber = room.getSeerCheckedSeatNumber();
        this.seerCheckedTeam = room.getSeerCheckedTeam();
        this.seerCheckedRole = room.getSeerCheckedRole();
        this.seerCheckedRoleName = room.getSeerCheckedRoleName();
        this.mechanicalWolfLearnedSeatNumber = room.getMechanicalWolfLearnedSeatNumber();
        this.mechanicalWolfLearnedRole = room.getMechanicalWolfLearnedRole();
        this.mechanicalWolfLearnedRoleName = room.getMechanicalWolfLearnedRoleName();
        this.mechanicalWolfSkillTargetSeatNumber = room.getMechanicalWolfSkillTargetSeatNumber();
        this.mechanicalWolfSkillResult = room.getMechanicalWolfSkillResult();
        this.mechanicalWolfPoisonUsed = room.isMechanicalWolfPoisonUsed();
        this.mechanicalWolfCanJoinWolfKill = room.isMechanicalWolfCanJoinWolfKill();
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
    public boolean isNightActionCompleted() { return nightActionCompleted; }
    public Integer getGuardTargetSeatNumber() { return guardTargetSeatNumber; }
    public Integer getPreviousGuardTargetSeatNumber() { return previousGuardTargetSeatNumber; }
    public Integer getWolfKillTargetSeatNumber() { return wolfKillTargetSeatNumber; }
    public String getWolfKillActorPlayerId() { return wolfKillActorPlayerId; }
    public boolean isWitchSavedWolfKill() { return witchSavedWolfKill; }
    public Integer getWitchPoisonTargetSeatNumber() { return witchPoisonTargetSeatNumber; }
    public boolean isWitchSaveUsed() { return witchSaveUsed; }
    public boolean isWitchPoisonUsed() { return witchPoisonUsed; }
    public List<Integer> getNightDeathSeatNumbers() { return nightDeathSeatNumbers; }
    public String getNightDeathMessage() { return nightDeathMessage; }
    public boolean isFirstDayNightReportReleased() { return firstDayNightReportReleased; }
    public List<Integer> getHunterCanShootSeatNumbers() { return hunterCanShootSeatNumbers; }
    public Integer getSeerCheckedSeatNumber() { return seerCheckedSeatNumber; }
    public String getSeerCheckedTeam() { return seerCheckedTeam; }
    public String getSeerCheckedRole() { return seerCheckedRole; }
    public String getSeerCheckedRoleName() { return seerCheckedRoleName; }
    public Integer getMechanicalWolfLearnedSeatNumber() { return mechanicalWolfLearnedSeatNumber; }
    public String getMechanicalWolfLearnedRole() { return mechanicalWolfLearnedRole; }
    public String getMechanicalWolfLearnedRoleName() { return mechanicalWolfLearnedRoleName; }
    public Integer getMechanicalWolfSkillTargetSeatNumber() { return mechanicalWolfSkillTargetSeatNumber; }
    public String getMechanicalWolfSkillResult() { return mechanicalWolfSkillResult; }
    public boolean isMechanicalWolfPoisonUsed() { return mechanicalWolfPoisonUsed; }
    public boolean isMechanicalWolfCanJoinWolfKill() { return mechanicalWolfCanJoinWolfKill; }
}
