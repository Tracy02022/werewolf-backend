package com.tracy.werewolf.dto;

public class MoveSeatRequest {
    private String playerId;
    private Integer seatNumber;

    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }
    public Integer getSeatNumber() { return seatNumber; }
    public void setSeatNumber(Integer seatNumber) { this.seatNumber = seatNumber; }
}
