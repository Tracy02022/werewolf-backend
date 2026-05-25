package com.tracy.werewolf.dto;

public class WitchActionRequest {
    private String playerId;
    private boolean useSave;
    private Integer poisonTargetSeatNumber;

    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }
    public boolean isUseSave() { return useSave; }
    public void setUseSave(boolean useSave) { this.useSave = useSave; }
    public Integer getPoisonTargetSeatNumber() { return poisonTargetSeatNumber; }
    public void setPoisonTargetSeatNumber(Integer poisonTargetSeatNumber) { this.poisonTargetSeatNumber = poisonTargetSeatNumber; }
}
