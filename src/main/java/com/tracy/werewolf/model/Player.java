package com.tracy.werewolf.model;
public class Player {
    private String id; private String name; private Role role; private boolean alive = true; private int seatNumber; private boolean host;
    public Player() {}
    public Player(String id, String name, int seatNumber, boolean host) { this.id=id; this.name=name; this.seatNumber=seatNumber; this.host=host; }
    public String getId(){return id;} public void setId(String id){this.id=id;}
    public String getName(){return name;} public void setName(String name){this.name=name;}
    public Role getRole(){return role;} public void setRole(Role role){this.role=role;}
    public boolean isAlive(){return alive;} public void setAlive(boolean alive){this.alive=alive;}
    public int getSeatNumber(){return seatNumber;} public void setSeatNumber(int seatNumber){this.seatNumber=seatNumber;}
    public boolean isHost(){return host;} public void setHost(boolean host){this.host=host;}
}
