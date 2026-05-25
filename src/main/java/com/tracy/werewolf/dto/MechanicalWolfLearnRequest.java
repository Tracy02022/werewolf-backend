package com.tracy.werewolf.dto;

public class MechanicalWolfLearnRequest {
    private String playerId;
    private Integer targetSeatNumber;

    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }
    public Integer getTargetSeatNumber() { return targetSeatNumber; }
    public void setTargetSeatNumber(Integer targetSeatNumber) { this.targetSeatNumber = targetSeatNumber; }
}
