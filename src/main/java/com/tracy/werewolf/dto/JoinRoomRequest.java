package com.tracy.werewolf.dto;

public class JoinRoomRequest {
    private String playerName;
    private Integer seatNumber;

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public Integer getSeatNumber() { return seatNumber; }
    public void setSeatNumber(Integer seatNumber) { this.seatNumber = seatNumber; }
}
