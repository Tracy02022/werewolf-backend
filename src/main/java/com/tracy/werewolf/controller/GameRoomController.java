package com.tracy.werewolf.controller;
import com.tracy.werewolf.dto.*; import com.tracy.werewolf.service.GameRoomService; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/rooms") @CrossOrigin(origins="*")
public class GameRoomController { private final GameRoomService service; public GameRoomController(GameRoomService service){this.service=service;}
 @PostMapping public GameRoomResponse createRoom(@RequestBody CreateRoomRequest req){return new GameRoomResponse(service.createRoom(req));}
 @PostMapping("/{roomCode}/join") public GameRoomResponse joinRoom(@PathVariable String roomCode,@RequestBody JoinRoomRequest req){return new GameRoomResponse(service.joinRoom(roomCode, req.getPlayerName()));}
 @PostMapping("/{roomCode}/fill-bots") public GameRoomResponse fillBots(@PathVariable String roomCode){return new GameRoomResponse(service.fillBots(roomCode));}
 @GetMapping("/{roomCode}") public GameRoomResponse getRoom(@PathVariable String roomCode){return new GameRoomResponse(service.getRoom(roomCode));}
 @PostMapping("/{roomCode}/start") public GameRoomResponse start(@PathVariable String roomCode,@RequestBody PlayerActionRequest req){return new GameRoomResponse(service.startGame(roomCode, req.getPlayerId()));}
 @PostMapping("/{roomCode}/next-phase") public GameRoomResponse next(@PathVariable String roomCode,@RequestBody PlayerActionRequest req){return new GameRoomResponse(service.nextPhase(roomCode, req.getPlayerId()));}
 @GetMapping("/{roomCode}/players/{playerId}/role") public RoleResponse role(@PathVariable String roomCode,@PathVariable String playerId){return new RoleResponse(service.getPlayerRole(roomCode, playerId));}
}
