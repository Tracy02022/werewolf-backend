package com.tracy.werewolf.service;

import com.tracy.werewolf.dto.CreateRoomRequest;
import com.tracy.werewolf.model.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameRoomService {
    private final Map<String, GameRoom> rooms = new HashMap<>();
    private final Random random = new Random();
    private final BoardService boardService;

    public GameRoomService(BoardService boardService) {
        this.boardService = boardService;
    }

    public GameRoom createRoom(CreateRoomRequest request) {
        int playerCount = request.getPlayerCount();
        validatePlayerCount(playerCount);
        validateHostName(request.getHostName());

        GameRoom room = new GameRoom();
        room.setRoomCode(generateUniqueRoomCode());
        room.setPlayerCount(playerCount);
        room.setPhase(GamePhase.WAITING);
        room.setRound(0);

        if (request.isCustomMode()) {
            if (playerCount < 10 || playerCount > 16) {
                throw new IllegalArgumentException("自选角色模式目前支持10-16人局");
            }
            Map<Role, Integer> cleanedRoles = cleanRoles(request.getCustomRoles());
            validateCustomRoles(cleanedRoles, playerCount);
            room.setCustomMode(true);
            room.setCustomRoles(cleanedRoles);
            room.setBoardId(null);
            room.setBoardName(playerCount + "人自选角色局");
        } else {
            if (request.getBoardId() == null || request.getBoardId().trim().isEmpty()) {
                throw new IllegalArgumentException("经典板子模式必须选择 boardId");
            }
            BoardDefinition board = boardService.getBoardById(request.getBoardId());
            if (board.getPlayerCount() != playerCount) {
                throw new IllegalArgumentException("选择的板子人数和房间人数不匹配");
            }
            room.setCustomMode(false);
            room.setBoardId(board.getId());
            room.setBoardName(board.getName());
            room.setCustomRoles(new LinkedHashMap<>(board.getRoles()));
        }

        String hostPlayerId = UUID.randomUUID().toString();
        Player host = new Player(hostPlayerId, request.getHostName().trim(), 1, true);
        room.setHostPlayerId(hostPlayerId);
        room.getPlayers().add(host);
        rooms.put(room.getRoomCode(), room);
        return room;
    }

    public GameRoom joinRoom(String roomCode, String playerName) {
        GameRoom room = getRoom(roomCode);
        if (room.getPhase() != GamePhase.WAITING) throw new IllegalStateException("Game already started");
        if (room.getPlayers().size() >= room.getPlayerCount()) throw new IllegalStateException("Room is full");
        if (playerName == null || playerName.trim().isEmpty()) throw new IllegalArgumentException("Player name is required");

        Player player = new Player(UUID.randomUUID().toString(), playerName.trim(), room.getPlayers().size() + 1, false);
        room.getPlayers().add(player);
        return room;
    }

    public GameRoom fillBots(String roomCode) {
        GameRoom room = getRoom(roomCode);
        if (room.getPhase() != GamePhase.WAITING) throw new IllegalStateException("Game already started");
        int index = 1;
        while (room.getPlayers().size() < room.getPlayerCount()) {
            Player bot = new Player(UUID.randomUUID().toString(), "Bot_" + index, room.getPlayers().size() + 1, false);
            room.getPlayers().add(bot);
            index++;
        }
        return room;
    }

    public GameRoom startGame(String roomCode, String playerId) {
        GameRoom room = getRoom(roomCode);
        if (!Objects.equals(room.getHostPlayerId(), playerId)) throw new IllegalStateException("Only host can start the game");
        if (room.getPhase() != GamePhase.WAITING) throw new IllegalStateException("Game already started");
        if (room.getPlayers().size() != room.getPlayerCount()) throw new IllegalStateException("Room is not full yet");

        assignRoles(room);
        room.setPhase(GamePhase.NIGHT);
        room.setRound(1);
        return room;
    }

    public GameRoom nextPhase(String roomCode, String playerId) {
        GameRoom room = getRoom(roomCode);
        if (!Objects.equals(room.getHostPlayerId(), playerId)) throw new IllegalStateException("Only host can control game phase");
        switch (room.getPhase()) {
            case WAITING -> throw new IllegalStateException("Game has not started");
            case NIGHT -> room.setPhase(GamePhase.DAY_DISCUSSION);
            case DAY_DISCUSSION -> room.setPhase(GamePhase.VOTING);
            case VOTING -> {
                if (isGameOver(room)) room.setPhase(GamePhase.FINISHED);
                else {
                    room.setPhase(GamePhase.NIGHT);
                    room.setRound(room.getRound() + 1);
                }
            }
            case FINISHED -> throw new IllegalStateException("Game already finished");
        }
        return room;
    }

    public GameRoom getRoom(String roomCode) {
        GameRoom room = rooms.get(roomCode);
        if (room == null) throw new IllegalArgumentException("Room not found");
        return room;
    }

    public Player getPlayerRole(String roomCode, String playerId) {
        GameRoom room = getRoom(roomCode);
        return room.getPlayers().stream().filter(p -> p.getId().equals(playerId)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));
    }

    private void assignRoles(GameRoom room) {
        List<Role> rolePool = new ArrayList<>();
        room.getCustomRoles().forEach((role, count) -> {
            for (int i = 0; i < count; i++) rolePool.add(role);
        });
        if (rolePool.size() != room.getPlayers().size()) throw new IllegalStateException("Role count does not match player count");
        Collections.shuffle(rolePool);
        for (int i = 0; i < room.getPlayers().size(); i++) {
            room.getPlayers().get(i).setRole(rolePool.get(i));
        }
    }

    private void validatePlayerCount(int playerCount) {
        if (playerCount < 9 || playerCount > 16) throw new IllegalArgumentException("Player count must be between 9 and 16");
    }

    private void validateHostName(String hostName) {
        if (hostName == null || hostName.trim().isEmpty()) throw new IllegalArgumentException("Host name is required");
    }

    private Map<Role, Integer> cleanRoles(Map<Role, Integer> roles) {
        if (roles == null || roles.isEmpty()) throw new IllegalArgumentException("自选角色不能为空");
        Map<Role, Integer> cleaned = new LinkedHashMap<>();
        roles.forEach((role, count) -> {
            if (role != null && count != null && count > 0) cleaned.put(role, count);
        });
        return cleaned;
    }

    private void validateCustomRoles(Map<Role, Integer> roles, int playerCount) {
        int total = roles.values().stream().mapToInt(Integer::intValue).sum();
        if (total != playerCount) throw new IllegalArgumentException("角色数量必须等于玩家人数，当前为 " + total + "/" + playerCount);

        int wolfCount = roles.entrySet().stream()
                .filter(e -> e.getKey().getCategory() == RoleCategory.WOLF)
                .mapToInt(Map.Entry::getValue)
                .sum();
        int goodCount = roles.entrySet().stream()
                .filter(e -> e.getKey().getCategory() == RoleCategory.GOOD)
                .mapToInt(Map.Entry::getValue)
                .sum();

        if (wolfCount < 3) throw new IllegalArgumentException("狼人阵营至少需要3名角色");
        if (wolfCount >= playerCount / 2.0) throw new IllegalArgumentException("狼人阵营数量过多，建议少于总人数一半");
        if (goodCount < 1) throw new IllegalArgumentException("至少需要1名好人阵营角色");
    }

    private boolean isGameOver(GameRoom room) {
        long aliveWolves = room.getPlayers().stream().filter(Player::isAlive)
                .filter(p -> p.getRole() != null && p.getRole().getCategory() == RoleCategory.WOLF).count();
        long aliveNonWolves = room.getPlayers().stream().filter(Player::isAlive)
                .filter(p -> p.getRole() == null || p.getRole().getCategory() != RoleCategory.WOLF).count();
        return aliveWolves == 0 || aliveWolves >= aliveNonWolves;
    }

    private String generateUniqueRoomCode() {
        String code;
        do { code = String.valueOf(100000 + random.nextInt(900000)); }
        while (rooms.containsKey(code));
        return code;
    }
}
