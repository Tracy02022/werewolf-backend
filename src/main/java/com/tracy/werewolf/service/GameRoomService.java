package com.tracy.werewolf.service;

import com.tracy.werewolf.dto.CreateRoomRequest;
import com.tracy.werewolf.model.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameRoomService {
    private static final long AUTO_ADVANCE_WAIT_MS = 15_000L;

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
        room.setCurrentNightAction(NightAction.NONE);
        room.setNightActionEndsAtEpochMs(0);
        room.setNightActionCompleted(false);

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

        int hostSeatNumber = requireSeatNumber(request.getSeatNumber());
        validateSeatAvailable(room, hostSeatNumber, null);

        String hostId = UUID.randomUUID().toString();
        Player host = new Player(hostId, request.getHostName().trim(), hostSeatNumber, true);
        room.setHostPlayerId(hostId);
        room.getPlayers().add(host);
        rooms.put(room.getRoomCode(), room);

        return sortPlayers(room);
    }

    public GameRoom joinRoom(String roomCode, String playerName, Integer seatNumber) {
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

        int selectedSeatNumber = requireSeatNumber(seatNumber);
        validateSeatAvailable(room, selectedSeatNumber, null);

        room.getPlayers().add(new Player(UUID.randomUUID().toString(), playerName.trim(), selectedSeatNumber, false));
        return sortPlayers(room);
    }

    public GameRoom fillBots(String roomCode) {
        GameRoom room = getRoom(roomCode);
        if (room.getPhase() != GamePhase.WAITING) {
            throw new IllegalStateException("Game already started");
        }
        while (room.getPlayers().size() < room.getPlayerCount()) {
            int seat = findFirstEmptySeat(room);
            room.getPlayers().add(new Player(UUID.randomUUID().toString(), "Bot" + seat, seat, false));
        }
        return sortPlayers(room);
    }

    public GameRoom moveSeat(String roomCode, String playerId, Integer seatNumber) {
        GameRoom room = getRoom(roomCode);
        if (room.getPhase() != GamePhase.WAITING) {
            throw new IllegalStateException("Only waiting players can change seats");
        }
        Player player = findPlayer(room, playerId);
        int selectedSeatNumber = requireSeatNumber(seatNumber);
        validateSeatAvailable(room, selectedSeatNumber, playerId);
        player.setSeatNumber(selectedSeatNumber);
        return sortPlayers(room);
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
        beginNight(room);
        return sortPlayers(room);
    }

    public GameRoom nextPhase(String roomCode, String playerId) {
        GameRoom room = getRoom(roomCode);
        requireHost(room, playerId, "Only host can control game phase");

        switch (room.getPhase()) {
            case WAITING -> throw new IllegalStateException("Game has not started");
            case NIGHT -> finishNightAndEnterDay(room);
            case SHERIFF_ELECTION -> {
                room.setFirstDayNightReportReleased(true);
                room.setPhase(GamePhase.DAY_DISCUSSION);
            }
            case DAY_DISCUSSION -> room.setPhase(GamePhase.VOTING);
            case VOTING -> {
                if (isGameOver(room)) {
                    room.setPhase(GamePhase.FINISHED);
                    room.setCurrentNightAction(NightAction.NONE);
                    room.setNightActionEndsAtEpochMs(0);
                    room.setNightActionCompleted(false);
                } else {
                    room.setPhase(GamePhase.NIGHT);
                    room.setRound(room.getRound() + 1);
                    beginNight(room);
                }
            }
            case FINISHED -> throw new IllegalStateException("Game already finished");
        }
        return sortPlayers(room);
    }

    public GameRoom advanceNightAction(String roomCode, String playerId) {
        GameRoom room = getRoom(roomCode);
        requireHost(room, playerId, "Only host can control night actions");
        if (room.getPhase() != GamePhase.NIGHT) {
            throw new IllegalStateException("Night action is only available at night");
        }
        advanceToNextNightAction(room);
        return sortPlayers(room);
    }

    public GameRoom skipNightAction(String roomCode, String playerId) {
        GameRoom room = getRoom(roomCode);
        if (room.getPhase() != GamePhase.NIGHT) {
            throw new IllegalStateException("Night action is only available at night");
        }
        if (room.isNightActionCompleted()) {
            return sortPlayers(room);
        }

        Player actor = findPlayer(room, playerId);
        if (!actor.isAlive()) {
            throw new IllegalStateException("Dead player cannot act");
        }
        if (!canPlayerActInCurrentNightAction(room, actor)) {
            throw new IllegalStateException("当前身份不能在这个夜间环节操作");
        }
        markNightActionCompleted(room);
        return sortPlayers(room);
    }

    public GameRoom voteOutPlayer(String roomCode, String hostPlayerId, Integer targetSeatNumber) {
        GameRoom room = getRoom(roomCode);
        requireHost(room, hostPlayerId, "Only host can mark voted player");
        if (room.getPhase() != GamePhase.VOTING && room.getPhase() != GamePhase.DAY_DISCUSSION) {
            throw new IllegalStateException("Only day voting phase can mark a voted player");
        }
        if (targetSeatNumber == null) {
            throw new IllegalArgumentException("Target seat number is required");
        }
        Player target = findAlivePlayerBySeat(room, targetSeatNumber);
        target.setAlive(false);
        return sortPlayers(room);
    }

    public GameRoom guardAction(String roomCode, String playerId, Integer targetSeatNumber) {
        GameRoom room = getRoom(roomCode);
        if (room.getPhase() != GamePhase.NIGHT || room.getCurrentNightAction() != NightAction.GUARD) {
            throw new IllegalStateException("Guard action is not available now");
        }
        if (room.isNightActionCompleted()) {
            return sortPlayers(room);
        }
        Player guard = findPlayer(room, playerId);
        if (!guard.isAlive() || !("GUARD".equals(guard.getRole()) || "AWAKENED_GUARD".equals(guard.getRole()))) {
            throw new IllegalStateException("Only alive guard can act now");
        }
        if (targetSeatNumber == null) {
            throw new IllegalArgumentException("Target seat number is required");
        }
        Player target = findAlivePlayerBySeat(room, targetSeatNumber);
        if (room.getPreviousGuardTargetSeatNumber() != null && room.getPreviousGuardTargetSeatNumber().equals(target.getSeatNumber())) {
            throw new IllegalStateException("守卫不能连续两晚守护同一名玩家");
        }
        room.setGuardTargetSeatNumber(target.getSeatNumber());
        room.setPreviousGuardTargetSeatNumber(target.getSeatNumber());
        markNightActionCompleted(room);
        return sortPlayers(room);
    }

    public GameRoom wolfKill(String roomCode, String playerId, Integer targetSeatNumber) {
        GameRoom room = getRoom(roomCode);
        if (room.getPhase() != GamePhase.NIGHT || room.getCurrentNightAction() != NightAction.WOLF_KILL) {
            throw new IllegalStateException("Wolf action is not available now");
        }
        if (room.isNightActionCompleted()) {
            return sortPlayers(room);
        }

        Player actor = findPlayer(room, playerId);
        if (!actor.isAlive()) {
            throw new IllegalStateException("Dead player cannot act");
        }
        if (!canPlayerPerformWolfKill(room, actor)) {
            throw new IllegalStateException("Only alive wolves can choose a kill target. Mechanical wolf can join wolf kill only after learning a wolf role.");
        }
        if (targetSeatNumber == null) {
            throw new IllegalArgumentException("Target seat number is required");
        }

        Player target = findAlivePlayerBySeat(room, targetSeatNumber);
        if (room.getWolfKillTargetSeatNumber() == null) {
            room.setWolfKillTargetSeatNumber(target.getSeatNumber());
            room.setWolfKillActorPlayerId(playerId);
            markNightActionCompleted(room);
        }
        return sortPlayers(room);
    }

    public GameRoom witchAction(String roomCode, String playerId, boolean useSave, Integer poisonTargetSeatNumber) {
        GameRoom room = getRoom(roomCode);
        if (room.getPhase() != GamePhase.NIGHT || room.getCurrentNightAction() != NightAction.WITCH) {
            throw new IllegalStateException("Witch action is not available now");
        }
        if (room.isNightActionCompleted()) {
            return sortPlayers(room);
        }

        Player witch = findPlayer(room, playerId);
        if (!witch.isAlive() || !"WITCH".equals(witch.getRole())) {
            throw new IllegalStateException("Only alive witch can act now");
        }

        if (useSave && poisonTargetSeatNumber != null) {
            throw new IllegalStateException("女巫同一晚不能同时使用解药和毒药");
        }

        if (useSave) {
            if (room.isWitchSaveUsed()) {
                throw new IllegalStateException("解药已经使用过，不能再次使用");
            }
            if (room.getWolfKillTargetSeatNumber() == null) {
                throw new IllegalStateException("今晚没有刀口，不能使用解药");
            }
            if (witch.getSeatNumber() == room.getWolfKillTargetSeatNumber()) {
                throw new IllegalStateException("女巫中刀不能自救");
            }
            room.setWitchSavedWolfKill(true);
            room.setWitchSaveUsed(true);
        }

        if (poisonTargetSeatNumber != null) {
            if (room.isWitchPoisonUsed()) {
                throw new IllegalStateException("毒药已经使用过，不能再次使用");
            }
            Player poisonTarget = findAlivePlayerBySeat(room, poisonTargetSeatNumber);
            room.setWitchPoisonTargetSeatNumber(poisonTarget.getSeatNumber());
            room.setWitchPoisonUsed(true);
        }

        markNightActionCompleted(room);
        return sortPlayers(room);
    }

    public GameRoom seerAction(String roomCode, String playerId, Integer targetSeatNumber) {
        GameRoom room = getRoom(roomCode);
        if (room.getPhase() != GamePhase.NIGHT || !(room.getCurrentNightAction() == NightAction.SEER || room.getCurrentNightAction() == NightAction.PSYCHIC)) {
            throw new IllegalStateException("Check action is not available now");
        }
        if (room.isNightActionCompleted()) {
            return sortPlayers(room);
        }

        Player actor = findPlayer(room, playerId);
        if (!actor.isAlive()) {
            throw new IllegalStateException("Dead player cannot act");
        }
        boolean isSeer = "SEER".equals(actor.getRole()) || "SKY_EYE".equals(actor.getRole()) || "AWAKENED_SEER".equals(actor.getRole());
        boolean isPsychic = "PSYCHIC".equals(actor.getRole());
        if (room.getCurrentNightAction() == NightAction.SEER && !isSeer) {
            throw new IllegalStateException("Only alive seer can act now");
        }
        if (room.getCurrentNightAction() == NightAction.PSYCHIC && !isPsychic) {
            throw new IllegalStateException("Only alive psychic can act now");
        }
        if (targetSeatNumber == null) {
            throw new IllegalArgumentException("Target seat number is required");
        }

        Player target = findAlivePlayerBySeat(room, targetSeatNumber);

        room.setSeerCheckedSeatNumber(target.getSeatNumber());
        if (isPsychic) {
            String psychicRole = getRoleSeenByPsychic(room, target);
            RoleInfo targetRole = roleCatalogService.getRole(psychicRole);
            room.setSeerCheckedRole(psychicRole);
            room.setSeerCheckedRoleName(targetRole.getName());
            room.setSeerCheckedTeam("具体身份");
        } else {
            String seerRole = getRoleSeenBySeer(room, target);
            RoleInfo targetRole = roleCatalogService.getRole(seerRole);
            room.setSeerCheckedTeam(targetRole.getTeam() == RoleTeam.WOLF ? "狼人" : "好人");
            room.setSeerCheckedRole(null);
            room.setSeerCheckedRoleName(null);
        }
        markNightActionCompleted(room);
        return sortPlayers(room);
    }

    public GameRoom mechanicalWolfLearn(String roomCode, String playerId, Integer targetSeatNumber) {
        GameRoom room = getRoom(roomCode);
        if (room.getPhase() != GamePhase.NIGHT || room.getCurrentNightAction() != NightAction.MECHANICAL_WOLF) {
            throw new IllegalStateException("Mechanical wolf action is not available now");
        }
        if (room.isNightActionCompleted()) {
            return sortPlayers(room);
        }

        Player actor = findPlayer(room, playerId);
        if (!actor.isAlive() || !"MECHANICAL_WOLF".equals(actor.getRole())) {
            throw new IllegalStateException("Only alive mechanical wolf can learn now");
        }
        if (targetSeatNumber == null) {
            throw new IllegalArgumentException("Target seat number is required");
        }

        Player target = findAlivePlayerBySeat(room, targetSeatNumber);
        RoleInfo learned = roleCatalogService.getRole(target.getRole());
        room.setMechanicalWolfLearnedSeatNumber(target.getSeatNumber());
        room.setMechanicalWolfLearnedRole(target.getRole());
        room.setMechanicalWolfLearnedRoleName(learned.getName());
        markNightActionCompleted(room);
        return sortPlayers(room);
    }

    public GameRoom mechanicalWolfSkill(String roomCode, String playerId, String skillType, Integer targetSeatNumber) {
        GameRoom room = getRoom(roomCode);
        if (room.getPhase() != GamePhase.NIGHT || room.getCurrentNightAction() != NightAction.MECHANICAL_WOLF) {
            throw new IllegalStateException("Mechanical wolf skill is not available now");
        }
        if (room.isNightActionCompleted()) {
            return sortPlayers(room);
        }

        Player actor = findPlayer(room, playerId);
        if (!actor.isAlive() || !"MECHANICAL_WOLF".equals(actor.getRole())) {
            throw new IllegalStateException("Only alive mechanical wolf can act now");
        }
        String learnedRole = room.getMechanicalWolfLearnedRole();
        if (learnedRole == null || learnedRole.isBlank()) {
            throw new IllegalStateException("Mechanical wolf has not learned a role yet");
        }

        String normalizedSkill = skillType == null ? "" : skillType.trim().toUpperCase();
        if (roleCatalogService.isWolfRole(learnedRole)) {
            room.setMechanicalWolfSkillResult("你学习的是狼人阵营身份。只有其他小狼全部出局后，才能在狼人刀人环节带刀。当前机械狼环节无需操作。");
            markNightActionCompleted(room);
            return sortPlayers(room);
        }

        if (("WITCH".equals(learnedRole) || "POISON".equals(normalizedSkill))) {
            if (room.isMechanicalWolfPoisonUsed()) {
                throw new IllegalStateException("机械狼学习到的毒药已经使用过");
            }
            Player target = findAlivePlayerBySeat(room, targetSeatNumber);
            room.setMechanicalWolfSkillTargetSeatNumber(target.getSeatNumber());
            room.setMechanicalWolfPoisonUsed(true);
            room.setMechanicalWolfPoisonUsedThisNight(true);
            room.setMechanicalWolfSkillResult("你使用了学习到的女巫毒药，目标是 " + target.getSeatNumber() + " 号。");
            markNightActionCompleted(room);
            return sortPlayers(room);
        }

        if ("GUARD".equals(learnedRole) || "AWAKENED_GUARD".equals(learnedRole) || "GUARD".equals(normalizedSkill)) {
            Player target = findAlivePlayerBySeat(room, targetSeatNumber);
            room.setMechanicalWolfSkillTargetSeatNumber(target.getSeatNumber());
            room.setMechanicalWolfSkillResult("你使用了学习到的守卫护盾，守护 " + target.getSeatNumber() + " 号。");
            markNightActionCompleted(room);
            return sortPlayers(room);
        }

        if ("SEER".equals(learnedRole) || "SKY_EYE".equals(learnedRole) || "AWAKENED_SEER".equals(learnedRole) || "SEER".equals(normalizedSkill)) {
            Player target = findAlivePlayerBySeat(room, targetSeatNumber);
            String seerRole = getRoleSeenBySeer(room, target);
            RoleInfo targetRole = roleCatalogService.getRole(seerRole);
            room.setMechanicalWolfSkillTargetSeatNumber(target.getSeatNumber());
            room.setMechanicalWolfSkillResult("你查验了 " + target.getSeatNumber() + " 号，结果是：" + (targetRole.getTeam() == RoleTeam.WOLF ? "狼人" : "好人") + "。");
            markNightActionCompleted(room);
            return sortPlayers(room);
        }

        if ("PSYCHIC".equals(learnedRole) || "PSYCHIC".equals(normalizedSkill)) {
            Player target = findAlivePlayerBySeat(room, targetSeatNumber);
            String psychicRole = getRoleSeenByPsychic(room, target);
            RoleInfo targetRole = roleCatalogService.getRole(psychicRole);
            room.setMechanicalWolfSkillTargetSeatNumber(target.getSeatNumber());
            room.setMechanicalWolfSkillResult("你通灵了 " + target.getSeatNumber() + " 号，具体身份是：" + targetRole.getName() + "。");
            markNightActionCompleted(room);
            return sortPlayers(room);
        }

        room.setMechanicalWolfSkillResult("你学习到的身份当前没有主动夜间技能，本环节自动跳过。");
        markNightActionCompleted(room);
        return sortPlayers(room);
    }

    public GameRoom getRoom(String roomCode) {
        GameRoom room = rooms.get(roomCode);
        if (room == null) {
            throw new IllegalArgumentException("Room not found");
        }
        autoAdvanceReadyNightActions(room);
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

    private void beginNight(GameRoom room) {
        room.setGuardTargetSeatNumber(null);
        room.setWolfKillTargetSeatNumber(null);
        room.setWolfKillActorPlayerId(null);
        room.setWitchSavedWolfKill(false);
        room.setWitchPoisonTargetSeatNumber(null);
        room.setNightDeathSeatNumbers(new ArrayList<>());
        room.setNightDeathMessage("");
        if (room.getRound() <= 1) {
            room.setFirstDayNightReportReleased(false);
        }
        room.setHunterCanShootSeatNumbers(new ArrayList<>());
        room.setSeerCheckedSeatNumber(null);
        room.setSeerCheckedTeam(null);
        room.setSeerCheckedRole(null);
        room.setSeerCheckedRoleName(null);
        room.setMechanicalWolfSkillTargetSeatNumber(null);
        room.setMechanicalWolfSkillResult(null);
        room.setMechanicalWolfPoisonUsedThisNight(false);
        room.setNightActionCompleted(false);
        room.setNightActionEndsAtEpochMs(0);
        startFirstAvailableNightAction(room);
    }

    private void startFirstAvailableNightAction(GameRoom room) {
        room.setCurrentNightAction(NightAction.NONE);
        advanceToNextNightAction(room);
    }

    private void advanceToNextNightAction(GameRoom room) {
        NightAction current = room.getCurrentNightAction();
        List<NightAction> order = List.of(
                NightAction.GUARD,
                NightAction.MECHANICAL_WOLF,
                NightAction.WOLF_KILL,
                NightAction.WITCH,
                NightAction.PSYCHIC,
                NightAction.SEER,
                NightAction.HUNTER_CHECK,
                NightAction.WHITE_GOD_CHECK,
                NightAction.MIXED_BLOOD_CHECK
        );

        int startIndex = current == NightAction.NONE ? -1 : order.indexOf(current);
        for (int i = startIndex + 1; i < order.size(); i++) {
            NightAction next = order.get(i);
            if (hasNightAction(room, next)) {
                enterNightAction(room, next);
                return;
            }
        }
        finishNightAndEnterDay(room);
    }

    private boolean hasNightAction(GameRoom room, NightAction action) {
        // 夜晚流程需要按板子固定播报：即使该身份玩家已经死亡，也要睁眼播报，然后 15 秒后自动跳过。
        return switch (action) {
            case GUARD -> hasAnyRole(room, "GUARD", "AWAKENED_GUARD");
            case MECHANICAL_WOLF -> hasAnyRole(room, "MECHANICAL_WOLF");
            case WOLF_KILL -> room.getPlayers().stream().anyMatch(p -> p.isAlive() && canPlayerPerformWolfKill(room, p));
            case WITCH -> hasAnyRole(room, "WITCH");
            case PSYCHIC -> hasAnyRole(room, "PSYCHIC");
            case SEER -> hasAnyRole(room, "SEER", "SKY_EYE", "AWAKENED_SEER");
            case HUNTER_CHECK -> hasAnyRole(room, "HUNTER");
            case WHITE_GOD_CHECK -> hasAnyRole(room, "IDIOT", "AWAKENED_FOOL");
            case MIXED_BLOOD_CHECK -> hasAnyRole(room, "MIXED_BLOOD", "SECRET_ADMIRER");
            default -> false;
        };
    }

    private void enterNightAction(GameRoom room, NightAction action) {
        room.setCurrentNightAction(action);
        room.setNightActionCompleted(false);
        room.setNightActionEndsAtEpochMs(0);

        if (action == NightAction.SEER || action == NightAction.PSYCHIC) {
            room.setSeerCheckedSeatNumber(null);
            room.setSeerCheckedTeam(null);
            room.setSeerCheckedRole(null);
            room.setSeerCheckedRoleName(null);
        }
        if (action == NightAction.MECHANICAL_WOLF) {
            room.setMechanicalWolfSkillTargetSeatNumber(null);
            room.setMechanicalWolfSkillResult(null);
        }

        // 没有技能释放的睁眼确认环节，或对应身份已阵亡/没有可行动者：播报后等待 15 秒自动进入下一环节。
        if (action == NightAction.HUNTER_CHECK
                || action == NightAction.WHITE_GOD_CHECK
                || action == NightAction.MIXED_BLOOD_CHECK
                || !hasAliveActorForNightAction(room, action)) {
            markNightActionCompleted(room);
        }
    }

    private void markNightActionCompleted(GameRoom room) {
        room.setNightActionCompleted(true);
        room.setNightActionEndsAtEpochMs(System.currentTimeMillis() + AUTO_ADVANCE_WAIT_MS);
    }

    private void finishNightAndEnterDay(GameRoom room) {
        resolveNightDeaths(room);
        if (room.getRound() <= 1 && !room.isFirstDayNightReportReleased()) {
            room.setPhase(GamePhase.SHERIFF_ELECTION);
        } else {
            room.setFirstDayNightReportReleased(true);
            room.setPhase(GamePhase.DAY_DISCUSSION);
        }
        room.setCurrentNightAction(NightAction.FINISHED);
        room.setNightActionEndsAtEpochMs(0);
        room.setNightActionCompleted(false);
    }

    private void resolveNightDeaths(GameRoom room) {
        Set<Integer> deaths = new LinkedHashSet<>();
        Set<Integer> poisonDeaths = new LinkedHashSet<>();

        Integer wolfTarget = room.getWolfKillTargetSeatNumber();
        Integer guardTarget = room.getGuardTargetSeatNumber();
        Integer mechanicalGuardTarget = getMechanicalWolfGuardTarget(room);

        if (wolfTarget != null) {
            boolean guarded = (guardTarget != null && guardTarget.equals(wolfTarget))
                    || (mechanicalGuardTarget != null && mechanicalGuardTarget.equals(wolfTarget));
            boolean saved = room.isWitchSavedWolfKill();

            // 同守同救：守卫和女巫解药作用于同一刀口，目标依然死亡。
            if ((!guarded && !saved) || (guarded && saved)) {
                deaths.add(wolfTarget);
            }
        }

        if (room.getWitchPoisonTargetSeatNumber() != null) {
            if (mechanicalGuardTarget == null || !mechanicalGuardTarget.equals(room.getWitchPoisonTargetSeatNumber())) {
                deaths.add(room.getWitchPoisonTargetSeatNumber());
                poisonDeaths.add(room.getWitchPoisonTargetSeatNumber());
            }
        }
        if (room.getMechanicalWolfSkillTargetSeatNumber() != null && room.isMechanicalWolfPoisonUsedThisNight()) {
            deaths.add(room.getMechanicalWolfSkillTargetSeatNumber());
            poisonDeaths.add(room.getMechanicalWolfSkillTargetSeatNumber());
        }

        List<Integer> actualDeaths = new ArrayList<>();
        List<Integer> hunterShootSeats = new ArrayList<>();
        for (Integer seat : deaths) {
            Optional<Player> playerOpt = room.getPlayers().stream()
                    .filter(p -> p.getSeatNumber() == seat && p.isAlive())
                    .findFirst();
            if (playerOpt.isEmpty()) continue;
            Player player = playerOpt.get();
            player.setAlive(false);
            actualDeaths.add(seat);
            if ("HUNTER".equals(player.getRole()) && !poisonDeaths.contains(seat)) {
                hunterShootSeats.add(seat);
            }
        }

        room.setNightDeathSeatNumbers(actualDeaths);
        room.setHunterCanShootSeatNumbers(hunterShootSeats);
        if (actualDeaths.isEmpty()) {
            room.setNightDeathMessage("昨夜是平安夜，没有玩家倒牌。");
        } else {
            room.setNightDeathMessage("昨夜倒牌玩家：" + joinSeatNumbers(actualDeaths) + "号。");
        }
    }

    private void autoAdvanceReadyNightActions(GameRoom room) {
        while (room.getPhase() == GamePhase.NIGHT
                && room.isNightActionCompleted()
                && room.getNightActionEndsAtEpochMs() > 0
                && System.currentTimeMillis() >= room.getNightActionEndsAtEpochMs()) {
            advanceToNextNightAction(room);
        }
    }

    private String getRoleSeenByPsychic(GameRoom room, Player target) {
        // 通灵师看具体身份；如果目标是机械狼且已经学习身份，则显示机械狼学到的身份。
        if ("MECHANICAL_WOLF".equals(target.getRole())
                && room.getMechanicalWolfLearnedRole() != null
                && !room.getMechanicalWolfLearnedRole().isBlank()) {
            return room.getMechanicalWolfLearnedRole();
        }
        return target.getRole();
    }

    private String getRoleSeenBySeer(GameRoom room, Player target) {
        // 预言家只看“好人/狼人”。机械狼若已学习身份，则按学习身份阵营给结果；未学习时按机械狼本体为狼人。
        if ("MECHANICAL_WOLF".equals(target.getRole())
                && room.getMechanicalWolfLearnedRole() != null
                && !room.getMechanicalWolfLearnedRole().isBlank()) {
            return room.getMechanicalWolfLearnedRole();
        }
        return target.getRole();
    }

    private Integer getMechanicalWolfGuardTarget(GameRoom room) {
        String learnedRole = room.getMechanicalWolfLearnedRole();
        if (("GUARD".equals(learnedRole) || "AWAKENED_GUARD".equals(learnedRole))
                && room.getMechanicalWolfSkillTargetSeatNumber() != null) {
            return room.getMechanicalWolfSkillTargetSeatNumber();
        }
        return null;
    }

    private boolean canPlayerActInCurrentNightAction(GameRoom room, Player player) {
        return switch (room.getCurrentNightAction()) {
            case GUARD -> "GUARD".equals(player.getRole()) || "AWAKENED_GUARD".equals(player.getRole());
            case MECHANICAL_WOLF -> "MECHANICAL_WOLF".equals(player.getRole());
            case WOLF_KILL -> canPlayerPerformWolfKill(room, player);
            case WITCH -> "WITCH".equals(player.getRole());
            case PSYCHIC -> "PSYCHIC".equals(player.getRole());
            case SEER -> "SEER".equals(player.getRole()) || "SKY_EYE".equals(player.getRole()) || "AWAKENED_SEER".equals(player.getRole());
            default -> false;
        };
    }

    private boolean hasAliveActorForNightAction(GameRoom room, NightAction action) {
        return room.getPlayers().stream()
                .filter(Player::isAlive)
                .anyMatch(player -> switch (action) {
                    case GUARD -> "GUARD".equals(player.getRole()) || "AWAKENED_GUARD".equals(player.getRole());
                    case MECHANICAL_WOLF -> "MECHANICAL_WOLF".equals(player.getRole());
                    case WOLF_KILL -> canPlayerPerformWolfKill(room, player);
                    case WITCH -> "WITCH".equals(player.getRole());
                    case PSYCHIC -> "PSYCHIC".equals(player.getRole());
                    case SEER -> "SEER".equals(player.getRole()) || "SKY_EYE".equals(player.getRole()) || "AWAKENED_SEER".equals(player.getRole());
                    default -> false;
                });
    }

    private boolean canPlayerPerformWolfKill(GameRoom room, Player player) {
        if (player == null || !player.isAlive() || player.getRole() == null) {
            return false;
        }

        if ("MECHANICAL_WOLF".equals(player.getRole())) {
            String learnedRole = room.getMechanicalWolfLearnedRole();
            return learnedRole != null
                    && roleCatalogService.isWolfRole(learnedRole)
                    && areAllOtherWolvesDead(room, player.getId());
        }

        return roleCatalogService.isWolfRole(player.getRole()) && !"MECHANICAL_WOLF".equals(player.getRole());
    }

    private boolean areAllOtherWolvesDead(GameRoom room, String mechanicalWolfPlayerId) {
        return room.getPlayers().stream()
                .filter(Player::isAlive)
                .filter(player -> !Objects.equals(player.getId(), mechanicalWolfPlayerId))
                .noneMatch(player -> roleCatalogService.isWolfRole(player.getRole()) && !"MECHANICAL_WOLF".equals(player.getRole()));
    }

    private boolean canMechanicalWolfJoinWolfKill(GameRoom room) {
        return room.getPlayers().stream()
                .filter(player -> "MECHANICAL_WOLF".equals(player.getRole()) && player.isAlive())
                .anyMatch(player -> canPlayerPerformWolfKill(room, player));
    }

    private boolean hasAnyRole(GameRoom room, String... roles) {
        Set<String> roleSet = new HashSet<>(Arrays.asList(roles));
        return room.getPlayers().stream().anyMatch(p -> roleSet.contains(p.getRole()));
    }

    private boolean hasAliveRole(GameRoom room, String role) {
        return room.getPlayers().stream().anyMatch(p -> p.isAlive() && role.equals(p.getRole()));
    }

    private boolean hasAliveAnyRole(GameRoom room, String... roles) {
        Set<String> roleSet = new HashSet<>(Arrays.asList(roles));
        return room.getPlayers().stream().anyMatch(p -> p.isAlive() && roleSet.contains(p.getRole()));
    }

    private String joinSeatNumbers(List<Integer> seats) {
        return seats.stream().map(String::valueOf).reduce((a, b) -> a + "、" + b).orElse("");
    }

    private void assignRoles(GameRoom room) {
        List<String> pool = buildRolePool(room);
        Collections.shuffle(pool);

        sortPlayers(room);
        for (int i = 0; i < room.getPlayers().size(); i++) {
            room.getPlayers().get(i).setRole(pool.get(i));
            room.getPlayers().get(i).setAlive(true);
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

    private Player findPlayer(GameRoom room, String playerId) {
        return room.getPlayers().stream()
                .filter(player -> Objects.equals(player.getId(), playerId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));
    }

    private Player findAlivePlayerBySeat(GameRoom room, int seatNumber) {
        return room.getPlayers().stream()
                .filter(player -> player.getSeatNumber() == seatNumber)
                .findFirst()
                .filter(Player::isAlive)
                .orElseThrow(() -> new IllegalArgumentException("Target seat is empty or player is already dead"));
    }

    private int requireSeatNumber(Integer seatNumber) {
        if (seatNumber == null) {
            throw new IllegalArgumentException("Seat number is required");
        }
        return seatNumber;
    }

    private void validateSeatAvailable(GameRoom room, int seatNumber, String currentPlayerId) {
        if (seatNumber < 1 || seatNumber > room.getPlayerCount()) {
            throw new IllegalArgumentException("Seat number must be between 1 and " + room.getPlayerCount());
        }

        boolean occupied = room.getPlayers().stream()
                .anyMatch(player -> player.getSeatNumber() == seatNumber
                        && !Objects.equals(player.getId(), currentPlayerId));

        if (occupied) {
            throw new IllegalArgumentException("This seat is already taken");
        }
    }

    private int findFirstEmptySeat(GameRoom room) {
        for (int seat = 1; seat <= room.getPlayerCount(); seat++) {
            final int currentSeat = seat;
            boolean occupied = room.getPlayers().stream()
                    .anyMatch(player -> player.getSeatNumber() == currentSeat);
            if (!occupied) {
                return seat;
            }
        }
        throw new IllegalStateException("No empty seat available");
    }

    private GameRoom sortPlayers(GameRoom room) {
        room.setMechanicalWolfCanJoinWolfKill(canMechanicalWolfJoinWolfKill(room));
        room.setPlayers(new ArrayList<>(room.getPlayers().stream()
                .sorted(Comparator.comparingInt(Player::getSeatNumber))
                .toList()));
        return room;
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
