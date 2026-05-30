package com.tracy.werewolf.dto;

public class MechanicalWolfSkillRequest {
    private String playerId;
    private String skillType;
    private Integer targetSeatNumber;

    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }
    public String getSkillType() { return skillType; }
    public void setSkillType(String skillType) { this.skillType = skillType; }
    public Integer getTargetSeatNumber() { return targetSeatNumber; }
    public void setTargetSeatNumber(Integer targetSeatNumber) { this.targetSeatNumber = targetSeatNumber; }
}
