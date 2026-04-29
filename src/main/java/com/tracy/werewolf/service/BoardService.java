package com.tracy.werewolf.service;

import com.tracy.werewolf.model.BoardDefinition;
import com.tracy.werewolf.model.Role;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BoardService {
    private final List<BoardDefinition> boards;

    public BoardService() {
        boards = List.of(
                board("classic_9", "9人经典局：预女猎", 9, "3狼3民 + 预言家、女巫、猎人。", roles(
                        Role.WEREWOLF, 3, Role.VILLAGER, 3, Role.SEER, 1, Role.WITCH, 1, Role.HUNTER, 1
                )),
                board("classic_12_guard", "12人经典：预女猎守", 12, "4狼4民 + 预言家、女巫、猎人、守卫。", roles(
                        Role.WEREWOLF, 4, Role.VILLAGER, 4, Role.SEER, 1, Role.WITCH, 1, Role.HUNTER, 1, Role.GUARD, 1
                )),
                board("classic_12_idiot", "12人经典：预女猎白", 12, "4狼4民 + 预言家、女巫、猎人、白痴。", roles(
                        Role.WEREWOLF, 4, Role.VILLAGER, 4, Role.SEER, 1, Role.WITCH, 1, Role.HUNTER, 1, Role.IDIOT, 1
                )),
                board("white_wolf_king_knight_12", "12人：白狼王骑士", 12, "白狼王 + 3狼人，对抗预言家、女巫、猎人、骑士。", roles(
                        Role.WHITE_WOLF_KING, 1, Role.WEREWOLF, 3, Role.VILLAGER, 4, Role.SEER, 1, Role.WITCH, 1, Role.HUNTER, 1, Role.KNIGHT, 1
                )),
                board("beauty_wolf_knight_12", "12人：狼美人骑士", 12, "狼美人 + 3狼人，对抗预言家、女巫、猎人、骑士。", roles(
                        Role.BEAUTY_WOLF, 1, Role.WEREWOLF, 3, Role.VILLAGER, 4, Role.SEER, 1, Role.WITCH, 1, Role.HUNTER, 1, Role.KNIGHT, 1
                )),
                board("gargoyle_gravekeeper_12", "12人：石像鬼守墓人", 12, "石像鬼 + 3狼人，对抗预言家、女巫、猎人、守墓人。", roles(
                        Role.GARGOYLE, 1, Role.WEREWOLF, 3, Role.VILLAGER, 4, Role.SEER, 1, Role.WITCH, 1, Role.HUNTER, 1, Role.GRAVE_KEEPER, 1
                )),
                board("mechanical_wolf_12", "12人：机械狼通灵师", 12, "机械狼 + 3狼人，对抗通灵师、女巫、猎人、守卫。", roles(
                        Role.MECHANICAL_WOLF, 1, Role.WEREWOLF, 3, Role.VILLAGER, 4, Role.PSYCHIC, 1, Role.WITCH, 1, Role.HUNTER, 1, Role.GUARD, 1
                ))
        );
    }

    public List<BoardDefinition> getBoards(Integer playerCount) {
        if (playerCount == null) return boards;
        return boards.stream().filter(b -> b.getPlayerCount() == playerCount).collect(Collectors.toList());
    }

    public BoardDefinition getBoardById(String id) {
        return boards.stream().filter(b -> b.getId().equals(id)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Board not found: " + id));
    }

    private BoardDefinition board(String id, String name, int playerCount, String description, Map<Role, Integer> roles) {
        return new BoardDefinition(id, name, playerCount, description, roles);
    }

    private Map<Role, Integer> roles(Object... values) {
        Map<Role, Integer> map = new LinkedHashMap<>();
        for (int i = 0; i < values.length; i += 2) {
            map.put((Role) values[i], (Integer) values[i + 1]);
        }
        return map;
    }
}
