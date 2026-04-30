package com.tracy.werewolf.service;
import com.tracy.werewolf.model.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class BoardService {
    private final List<Board> boards = List.of(
            new Board("classic_9", "9人标准局", 9, Map.of(
                    "WEREWOLF", 3,
                    "VILLAGER", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1
            ), "适合新手和快速局：3狼、3民、预言家、女巫、猎人。"),

            new Board("classic_12_guard", "12人经典：预女猎守", 12, Map.of(
                    "WEREWOLF", 4,
                    "VILLAGER", 4,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "GUARD", 1
            ), "最经典的12人局：4狼、4民、预言家、女巫、猎人、守卫。"),

            new Board("white_wolf_knight_12", "12人：白狼王骑士", 12, Map.of(
                    "WEREWOLF", 3,
                    "WHITE_WOLF_KING", 1,
                    "VILLAGER", 4,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "KNIGHT", 1
            ), "白狼王 + 骑士板子，适合进阶玩家。"),

            new Board("beauty_wolf_knight_12", "12人：狼美人骑士", 12, Map.of(
                    "WEREWOLF", 3,
                    "BEAUTY_WOLF", 1,
                    "VILLAGER", 4,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "KNIGHT", 1
            ), "狼美人 + 骑士，戏剧性更强。"),

            new Board("gargoyle_grave_12", "12人：石像鬼守墓人", 12, Map.of(
                    "WEREWOLF", 3,
                    "GARGOYLE", 1,
                    "VILLAGER", 4,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "GRAVE_KEEPER", 1
            ), "石像鬼 + 守墓人，信息量较高。"),

            new Board("mechanical_wolf_12", "12人：机械狼通灵师", 12, Map.of(
                    "WEREWOLF", 3,
                    "MECHANICAL_WOLF", 1,
                    "VILLAGER", 4,
                    "PSYCHIC", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "GUARD", 1
            ), "机械狼 + 通灵师，适合花板子。")
    );

    public List<Board> getBoards(Integer playerCount) {
        if (playerCount == null) {
            return boards;
        }
        return boards.stream()
                .filter(board -> board.getPlayerCount() == playerCount)
                .toList();
    }

    public Board getBoard(String id) {
        return boards.stream()
                .filter(board -> board.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));
    }
}
