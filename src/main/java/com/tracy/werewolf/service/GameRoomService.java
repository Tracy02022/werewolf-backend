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
    private final RoleCatalogService roleCatalogService;

    public GameRoomService(BoardService boardService, RoleCatalogService roleCatalogService) {
        this.boardService = boardService;
        this.roleCatalogService = roleCatalogService;
    }

    public GameRoom createRoom(CreateRoomRequest request) {
        int playerCount = request.getPlayerCount();
        if (playerCount < 9 || playerCount > 16) {
            throw new IllegalArgumentException("Player count must be between 9 and 16");
        }
        if (request.getHostName() == null || request.getHostName().trim().isEmpty()) {
            throw new IllegalArgumentException("Host name is required");
        }

        GameRoom room = new GameRoom();
        room.setRoomCode(generateUniqueRoomCode());
        room.setPlayerCount(playerCount);
        room.setPhase(GamePhase.WAITING);
        room.setRound(0);

        if (request.isCustomMode()) {
            validateCustomRoles(request.getCustomRoles(), playerCount);
            room.setCustomMode(true);
            room.setCustomRoles(cleanRoles(request.getCustomRoles()));
        } else {
            Board board = boardService.getBoard(request.getBoardId());
            if (board.getPlayerCount() != playerCount) {
                throw new IllegalArgumentException("Board does not match player count");
            }
            room.setBoardId(board.getId());
            room.setCustomMode(false);
        }

        String hostId = UUID.randomUUID().toString();
        Player host = new Player(hostId, request.getHostName().trim(), 1, true);
        room.setHostPlayerId(hostId);
        room.getPlayers().add(host);
        rooms.put(room.getRoomCode(), room);

        return room;
    }

    public GameRoom joinRoom(String roomCode, String playerName) {
        GameRoom room = getRoom(roomCode);
        if (room.getPhase() != GamePhase.WAITING) {
            throw new IllegalStateException("Game already started");
        }
        if (room.getPlayers().size() >= room.getPlayerCount()) {
            throw new IllegalStateException("Room is full");
        }
        if (playerName == null || playerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Player name is required");
        }

        room.getPlayers().add(new Player(UUID.randomUUID().toString(), playerName.trim(), room.getPlayers().size() + 1, false));
        return room;
    }

    public GameRoom fillBots(String roomCode) {
        GameRoom room = getRoom(roomCode);
        if (room.getPhase() != GamePhase.WAITING) {
            throw new IllegalStateException("Game already started");
        }
        while (room.getPlayers().size() < room.getPlayerCount()) {
            room.getPlayers().add(new Player(UUID.randomUUID().toString(), "Bot " + (room.getPlayers().size() + 1), room.getPlayers().size() + 1, false));
        }
        return room;
    }

    public GameRoom startGame(String roomCode, String playerId) {
        GameRoom room = getRoom(roomCode);
        requireHost(room, playerId, "Only host can start the game");
        if (room.getPhase() != GamePhase.WAITING) {
            throw new IllegalStateException("Game already started");
        }
        if (room.getPlayers().size() != room.getPlayerCount()) {
            throw new IllegalStateException("Room is not full yet");
        }

        assignRoles(room);
        room.setPhase(GamePhase.NIGHT);
        room.setRound(1);
        return room;
    }

    public GameRoom nextPhase(String roomCode, String playerId) {
        GameRoom room = getRoom(roomCode);
        requireHost(room, playerId, "Only host can control game phase");

        switch (room.getPhase()) {
            case WAITING -> throw new IllegalStateException("Game has not started");
            case NIGHT -> room.setPhase(GamePhase.DAY_DISCUSSION);
            case DAY_DISCUSSION -> room.setPhase(GamePhase.VOTING);
            case VOTING -> {
                if (isGameOver(room)) {
                    room.setPhase(GamePhase.FINISHED);
                } else {
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
        if (room == null) {
            throw new IllegalArgumentException("Room not found");
        }
        return room;
    }

    public String getPlayerRole(String roomCode, String playerId) {
        return getRoom(roomCode).getPlayers().stream()
                .filter(player -> player.getId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Player not found"))
                .getRole();
    }

    public RoleInfo getPlayerRoleInfo(String roomCode, String playerId) {
        String role = getPlayerRole(roomCode, playerId);
        if (role == null) {
            throw new IllegalStateException("Role has not been assigned yet");
        }
        return roleCatalogService.getRole(role);
    }

    private void assignRoles(GameRoom room) {
        List<String> pool = buildRolePool(room);
        Collections.shuffle(pool);

        for (int i = 0; i < room.getPlayers().size(); i++) {
            room.getPlayers().get(i).setRole(pool.get(i));
        }
    }

    private List<String> buildRolePool(GameRoom room) {
        Map<String, Integer> map = room.isCustomMode()
                ? room.getCustomRoles()
                : boardService.getBoard(room.getBoardId()).getRoles();

        List<String> pool = new ArrayList<>();
        map.forEach((role, count) -> {
            for (int i = 0; i < count; i++) {
                pool.add(role);
            }
        });
        return pool;
    }

    private void validateCustomRoles(Map<String, Integer> roles, int playerCount) {
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("Custom roles are required");
        }

        int total = roles.values().stream()
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();

        if (total != playerCount) {
            throw new IllegalArgumentException("Role total must equal player count");
        }

        for (Map.Entry<String, Integer> entry : roles.entrySet()) {
            if (entry.getValue() != null && entry.getValue() > 0 && !roleCatalogService.exists(entry.getKey())) {
                throw new IllegalArgumentException("Unknown role: " + entry.getKey());
            }
        }

        int wolves = roles.entrySet().stream()
                .filter(entry -> roleCatalogService.isWolfRole(entry.getKey()))
                .mapToInt(entry -> entry.getValue() == null ? 0 : entry.getValue())
                .sum();

        if (wolves < 3) {
            throw new IllegalArgumentException("At least 3 wolf team roles are required");
        }

        if (wolves >= playerCount / 2.0) {
            throw new IllegalArgumentException("Wolf team roles should be less than half of players");
        }

        int goodOrSpecial = roles.entrySet().stream()
                .filter(entry -> !roleCatalogService.isWolfRole(entry.getKey()))
                .mapToInt(entry -> entry.getValue() == null ? 0 : entry.getValue())
                .sum();

        if (goodOrSpecial < 1) {
            throw new IllegalArgumentException("At least 1 non-wolf role is required");
        }
    }

    private Map<String, Integer> cleanRoles(Map<String, Integer> roles) {
        Map<String, Integer> out = new LinkedHashMap<>();
        roles.forEach((role, count) -> {
            if (count != null && count > 0) {
                out.put(role, count);
            }
        });
        return out;
    }

    private boolean isGameOver(GameRoom room) {
        long wolves = room.getPlayers().stream()
                .filter(Player::isAlive)
                .filter(player -> roleCatalogService.isWolfRole(player.getRole()))
                .count();

        long others = room.getPlayers().stream()
                .filter(Player::isAlive)
                .filter(player -> !roleCatalogService.isWolfRole(player.getRole()))
                .count();

        return wolves == 0 || wolves >= others;
    }

    private void requireHost(GameRoom room, String playerId, String message) {
        if (!Objects.equals(room.getHostPlayerId(), playerId)) {
            throw new IllegalStateException(message);
        }
    }

    private String generateUniqueRoomCode() {
        String code;
        do {
            code = String.valueOf(100000 + random.nextInt(900000));
        } while (rooms.containsKey(code));
        return code;
    }
}
