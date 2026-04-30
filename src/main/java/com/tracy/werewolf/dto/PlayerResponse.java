package com.tracy.werewolf.dto; public class PlayerResponse { private String id; private String name; private boolean alive; private int seatNumber; private boolean host;
 public PlayerResponse(String id,String name,boolean alive,int seatNumber,boolean host){this.id=id;this.name=name;this.alive=alive;this.seatNumber=seatNumber;this.host=host;}
 public String getId(){return id;} public String getName(){return name;} public boolean isAlive(){return alive;} public int getSeatNumber(){return seatNumber;} public boolean isHost(){return host;}
}