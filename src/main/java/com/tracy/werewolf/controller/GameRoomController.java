package com.tracy.werewolf.controller;

import com.tracy.werewolf.dto.CreateRoomRequest;
import com.tracy.werewolf.dto.JoinRoomRequest;
import com.tracy.werewolf.dto.PlayerActionRequest;
import com.tracy.werewolf.model.GameRoom;
import com.tracy.werewolf.model.Player;
import com.tracy.werewolf.service.GameRoomService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*")
public class GameRoomController {
    private final GameRoomService gameRoomService;
    public GameRoomController(GameRoomService gameRoomService) { this.gameRoomService = gameRoomService; }

    @PostMapping
    public GameRoom createRoom(@RequestBody CreateRoomRequest request) { return gameRoomService.createRoom(request); }

    @PostMapping("/{roomCode}/join")
    public GameRoom joinRoom(@PathVariable String roomCode, @RequestBody JoinRoomRequest request) {
        return gameRoomService.joinRoom(roomCode, request.getPlayerName());
    }

    @PostMapping("/{roomCode}/fill-bots")
    public GameRoom fillBots(@PathVariable String roomCode) { return gameRoomService.fillBots(roomCode); }

    @GetMapping("/{roomCode}")
    public GameRoom getRoom(@PathVariable String roomCode) { return gameRoomService.getRoom(roomCode); }

    @PostMapping("/{roomCode}/start")
    public GameRoom startGame(@PathVariable String roomCode, @RequestBody PlayerActionRequest request) {
        return gameRoomService.startGame(roomCode, request.getPlayerId());
    }

    @PostMapping("/{roomCode}/next-phase")
    public GameRoom nextPhase(@PathVariable String roomCode, @RequestBody PlayerActionRequest request) {
        return gameRoomService.nextPhase(roomCode, request.getPlayerId());
    }

    @GetMapping("/{roomCode}/players/{playerId}/role")
    public Player getMyRole(@PathVariable String roomCode, @PathVariable String playerId) {
        return gameRoomService.getPlayerRole(roomCode, playerId);
    }
}
