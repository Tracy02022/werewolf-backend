package com.tracy.werewolf.service;

import com.tracy.werewolf.model.Board;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BoardService {

    private static Map<String, Integer> roles(Object... values) {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (int i = 0; i < values.length; i += 2) {
            map.put((String) values[i], (Integer) values[i + 1]);
        }
        return map;
    }

    private final List<Board> boards = List.of(
            new Board("b_444_seer_witch_hunter_idiot", "444预女猎白", 12, roles(
                    "WEREWOLF", 4,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "IDIOT", 1,
                    "VILLAGER", 4
            ), "标准基础板，神职配置经典，适合新手入门。白痴可替换为九尾狐。"),

            new Board("b_4431_secret_admirer", "4431预女猎白暗恋者", 12, roles(
                    "SECRET_ADMIRER", 1,
                    "WEREWOLF", 4,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "IDIOT", 1,
                    "VILLAGER", 3
            ), "面杀规则下暗恋对象出局可猜暗恋者，猜对则暗恋者替出。"),

            new Board("b_4431_mixed_blood", "4431预女猎白混", 12, roles(
                    "MIXED_BLOOD", 1,
                    "WEREWOLF", 4,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "IDIOT", 1,
                    "VILLAGER", 3
            ), "混血儿俗称混混，算村民阵营，帮主人取胜。"),

            new Board("b_444_silent_elder_knight", "444禁骑", 12, roles(
                    "WEREWOLF", 4,
                    "SEER", 1,
                    "WITCH", 1,
                    "KNIGHT", 1,
                    "SILENT_ELDER", 1,
                    "VILLAGER", 4
            ), "禁言长老可变种为禁票长老。骑士可决斗场上任意玩家。"),

            new Board("b_444_silent_elder_stalker", "444禁潜", 12, roles(
                    "WEREWOLF", 4,
                    "SEER", 1,
                    "WITCH", 1,
                    "STALKER", 1,
                    "SILENT_ELDER", 1,
                    "VILLAGER", 4
            ), "潜行者在放逐投票后，若所投玩家未被放逐，可当晚暗杀，全场一次。"),

            new Board("b_444_dark_warlock_silent", "444暗禁", 12, roles(
                    "DARK_WARLOCK", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "STALKER", 1,
                    "SILENT_ELDER", 1,
                    "VILLAGER", 4
            ), "暗夜术士可挽救被神民技能杀死的玩家，使用后不再获知夜里额外死亡信息。"),

            new Board("b_444_butterfly", "444花蝴蝶", 12, roles(
                    "WEREWOLF", 4,
                    "SEER", 1,
                    "BUTTERFLY", 1,
                    "STALKER", 1,
                    "HUNTER", 1,
                    "VILLAGER", 4
            ), "花蝴蝶最多使用2次，抱到狼人则全狼刀人失效。"),

            new Board("b_444_white_wolf_guard", "444白狼王&守卫", 12, roles(
                    "WHITE_WOLF_KING", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "GUARD", 1,
                    "VILLAGER", 4
            ), "白狼王自爆可带走任意玩家；守卫同守同救算死亡。猎人可替换为骑士。"),

            new Board("b_444_rusty_sword_knight", "444锈剑骑士", 12, roles(
                    "WHITE_WOLF_KING", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "RUSTY_SWORD_KNIGHT", 1,
                    "GUARD", 1,
                    "VILLAGER", 4
            ), "锈剑骑士死于狼刀后，左边第一只狼人死亡，中间玩家均为好人。"),

            new Board("b_444_gambler", "444赌徒", 12, roles(
                    "WHITE_WOLF_KING", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "GAMBLER", 1,
                    "GUARD", 1,
                    "VILLAGER", 4
            ), "赌徒下注狼则狼死，下注好人则赌徒死，全场一次。"),

            new Board("b_444_beauty_wolf_guard", "444狼美人&守卫", 12, roles(
                    "BEAUTY_WOLF", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "GUARD", 1,
                    "VILLAGER", 4
            ), "狼美人死亡时被魅惑玩家殉情，殉情的猎人不能开枪。猎人可替换为骑士。"),

            new Board("b_444_demon_guard", "444恶魔&守卫", 12, roles(
                    "DEMON", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "GUARD", 1,
                    "VILLAGER", 4
            ), "恶魔可验场上好人是神民还是平民；免疫毒药和夜晚猎人的枪。"),

            new Board("b_444_evil_knight_guard", "444恶灵骑士&守卫", 12, roles(
                    "EVIL_KNIGHT", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "GUARD", 1,
                    "VILLAGER", 4
            ), "恶灵骑士免疫毒药/夜晚猎人枪，对其使用技能者死亡，全场仅触发一次反弹。"),

            new Board("b_444_nightmare_guard", "444梦魇&守卫", 12, roles(
                    "NIGHTMARE", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "GUARD", 1,
                    "VILLAGER", 4
            ), "梦魇每晚恐惧一人，可令预言家/女巫/猎人/守卫当晚技能失效。"),

            new Board("b_444_nightmare_bewitcher", "444梦魇&蛊惑师", 12, roles(
                    "NIGHTMARE", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "BEWITCHER", 1,
                    "GUARD", 1,
                    "VILLAGER", 4
            ), "蛊惑师每晚指定一人，若当晚自身死亡则该人代替死亡。"),

            new Board("b_444_beauty_wolf_gangster", "444狼美人&老流氓", 12, roles(
                    "BEAUTY_WOLF", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "IDIOT", 1,
                    "GANGSTER", 1,
                    "VILLAGER", 3
            ), "老流氓免疫魅惑，被毒/枪击后当天不死，次日发言结束后死亡。"),

            new Board("b_444_wolf_king_dream_eater", "444狼王&摄梦人", 12, roles(
                    "WOLF_KING", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "DREAM_EATER", 1,
                    "VILLAGER", 4
            ), "狼王与猎人死后均不翻牌直接开枪；摄梦人连续两晚同一玩家则梦游者死亡。"),

            new Board("b_444_evil_knight_dream_eater", "444恶灵骑士&摄梦人", 12, roles(
                    "EVIL_KNIGHT", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "DREAM_EATER", 1,
                    "VILLAGER", 4
            ), "体验两极化，实力差距大时需酌情限制技能。"),

            new Board("b_444_shadow_of_nightmare_dream_eater", "444噩梦之影&摄梦人", 12, roles(
                    "SHADOW_OF_NIGHTMARE", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "DREAM_EATER", 1,
                    "VILLAGER", 4
            ), "噩梦之影不能连续两晚恐惧同一人，首夜与狼人互不相认。"),

            new Board("b_444_wolf_king_magician", "444狼王/火狼&魔术师", 12, roles(
                    "WOLF_KING", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "MAGICIAN", 1,
                    "VILLAGER", 4
            ), "狼王可替换为火狼；火狼在狼队倒牌后可永久封禁一名玩家技能；魔术师每晚交换两人号码。"),

            new Board("b_444_big_gray_wolf_prophet", "444大灰狼&占卜师", 12, roles(
                    "BIG_GRAY_WOLF", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "PROPHET", 1,
                    "VILLAGER", 4
            ), "大灰狼单独行动，女巫看不见大灰狼刀法；占卜师可全场一次标记3人限制刀范围。"),

            new Board("b_444_hidden_wolf_crow", "444隐狼&乌鸦", 12, roles(
                    "HIDDEN_WOLF", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "CROW", 1,
                    "VILLAGER", 4
            ), "隐狼被预言家验为好人，狼同伴不认识隐狼；乌鸦每晚诅咒一人使其被多票一票。"),

            new Board("b_444_bear_tamer_hidden_wolf", "444驯熊师&隐狼", 12, roles(
                    "HIDDEN_WOLF", 1,
                    "WEREWOLF", 3,
                    "BEAR_TAMER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "IDIOT", 1,
                    "VILLAGER", 4
            ), "弱化版隐狼，狼全灭时隐狼也死；驯熊师通过熊咆哮判断身边是否有狼。"),

            new Board("b_3441_wild_child", "3441野孩子", 12, roles(
                    "WILD_CHILD", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "IDIOT", 1,
                    "VILLAGER", 4
            ), "野孩子以榜样为参照，榜样出局则野孩子变狼加入狼队。"),

            new Board("b_444_bomber", "444炸弹人", 12, roles(
                    "WEREWOLF", 4,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "BOMBER", 1,
                    "VILLAGER", 4
            ), "炸弹人被放逐后可翻牌炸死所有投票给自己的玩家，被炸死者不能发动技能。"),

            new Board("b_444_black_merchant_elder", "444黑商&大树", 12, roles(
                    "WHITE_WOLF_KING", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "BLACK_MERCHANT", 1,
                    "ELDER", 1,
                    "VILLAGER", 3
            ), "黑商可赠予验/枪/毒技能，给到狼则黑商天亮死亡；大树需被刀两次才死，其他死因让全神失去技能。"),

            new Board("b_444_black_merchant_wolf_brothers", "444黑商&狼兄弟", 12, roles(
                    "WOLF_BROTHER_ELDER", 1,
                    "WOLF_BROTHER_YOUNGER", 1,
                    "WEREWOLF", 2,
                    "SEER", 1,
                    "WITCH", 1,
                    "BLACK_MERCHANT", 1,
                    "IDIOT", 1,
                    "VILLAGER", 4
            ), "狼兄死后狼弟获得额外杀人能力；狼弟在加入狼队前不被验出。白痴可替换为守卫。"),

            new Board("b_444_zoo", "444动物园", 12, roles(
                    "WOLF_KING", 1,
                    "WEREWOLF", 3,
                    "BEAR", 1,
                    "PENGUIN", 1,
                    "CROW", 1,
                    "FOX", 1,
                    "RABBIT", 4
            ), "娱乐板子，全角色以动物命名，各有特殊技能。"),

            new Board("b_444_animal_dream", "444动物梦境", 12, roles(
                    "BEAUTY_WOLF", 1,
                    "WEREWOLF", 3,
                    "BEAR", 1,
                    "LITTLE_FOX", 1,
                    "WHITE_CAT", 1,
                    "PUFFERFISH", 1,
                    "ALPACA", 4
            ), "首夜无刀，白猫出局后额外存活至下次投票，河豚可带走投票者。"),

            new Board("b_444_seed_wolf", "444种狼", 12, roles(
                    "SEED_WOLF", 1,
                    "HIDDEN_WOLF", 1,
                    "WEREWOLF", 2,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "GUARD", 1,
                    "VILLAGER", 4
            ), "种狼可感染被击杀目标转化为狼人，全场一次，女巫救或守卫守则感染失效。"),

            new Board("b_4431_sky_eye_prayer", "4431天眼&祈求者", 12, roles(
                    "PRAYER", 1,
                    "DEMON", 1,
                    "WEREWOLF", 3,
                    "SKY_EYE", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "VILLAGER", 4
            ), "祈求者祈求到不同身份获得不同技能，祈求到狼则成为第三方。"),

            new Board("b_345_thief_cupid", "345盗丘", 14, roles(
                    "THIEF", 1,
                    "CUPID", 1,
                    "WOLF_KING", 1,
                    "WEREWOLF", 2,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "IDIOT", 1,
                    "VILLAGER", 5
            ), "14张牌，盗贼选身份，丘比特连情侣；情侣阵营根据双方身份而定。狼王可替换为狼人，白痴可替换为守卫。"),

            new Board("b_444_fahai", "444法海", 12, roles(
                    "WEREWOLF", 4,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "CUPID", 1,
                    "FAHAI", 1,
                    "VILLAGER", 3
            ), "法海每晚可检验并剪断情侣链，本局丘比特属于神职阵营。"),

            new Board("b_3441_succubus_thief", "3441魅魔&盗贼", 12, roles(
                    "THIEF", 1,
                    "SUCCUBUS", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "GUARD", 1,
                    "VILLAGER", 4
            ), "魅魔属于狼队，首夜刀人后连一个好人为情侣，变成第三方屠城获胜。"),

            new Board("b_3441_ghost_bride_thief", "3441鬼魂新娘&盗贼", 12, roles(
                    "THIEF", 1,
                    "GHOST_BRIDE", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "GUARD", 1,
                    "VILLAGER", 4
            ), "鬼魂新娘首夜选新郎，第三方屠城获胜，单身狼全灭后情侣可继续刀人。"),

            new Board("b_444_infinite_firepower", "444无限火力", 12, roles(
                    "WHITE_WOLF_KING", 1,
                    "DEMON", 1,
                    "BEAUTY_WOLF", 1,
                    "HIDDEN_WOLF", 1,
                    "FOX", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "GUARD", 1,
                    "IDIOT", 1,
                    "ELDER", 1,
                    "SAPLING", 2
            ), "娱乐板子，大树即长老，树苗即平民，所有树苗死亡大树随之死亡。"),

            new Board("b_444_magic_wolf_demon_hunter", "444魔狼&猎魔人", 12, roles(
                    "MAGIC_WOLF", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "DEMON_HUNTER", 1,
                    "IDIOT", 1,
                    "VILLAGER", 4
            ), "魔狼自爆后封印所有神牌；猎魔人免疫毒药，每晚狩猎猜狼人。"),

            new Board("b_3441_godfather_cupid_piper", "3441狼教父&丘比特&吹笛者", 12, roles(
                    "CUPID", 1,
                    "PIPER", 1,
                    "WOLF_GODFATHER", 1,
                    "WEREWOLF", 2,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "VILLAGER", 4
            ), "适合高配局，吹笛者全部魅惑则单独获胜；狼教父可复活并转化被刀玩家。"),

            new Board("b_4602_cupid_romance", "4602丘比特奇缘", 12, roles(
                    "CUPID", 1,
                    "SECRET_ADMIRER", 1,
                    "SHADOW_OF_NIGHTMARE", 1,
                    "WOLF_WITCH", 1,
                    "TIME_ECLIPSE_WOLF_PRINCESS", 1,
                    "WOLF_KING", 1,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "IDIOT", 1,
                    "DREAM_EATER", 1,
                    "CROW", 1
            ), "娱乐板子，屠城规则，双爆吞警徽，发言时长120秒。"),

            new Board("b_444_shadow_avenger", "444影子&复仇者", 12, roles(
                    "SHADOW", 1,
                    "AVENGER", 1,
                    "MIXED_BLOOD", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "GUARD", 1,
                    "VILLAGER", 3
            ), "三张可变阵营牌，适应11-16人局，逻辑线丰富，变化多。"),

            new Board("b_444_hateful_butcher_dream_eater", "444憎恨屠夫&摄梦人", 12, roles(
                    "HATEFUL_BUTCHER", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "DREAM_EATER", 1,
                    "VILLAGER", 4
            ), "憎恨屠夫可抵抗神民技能一次，被放逐后令投票神民封印一轮，自爆可永久封禁一人技能。"),

            new Board("b_444_witch_wolf_guard", "444魔女&守卫", 12, roles(
                    "WITCH_WOLF", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "GUARD", 1,
                    "VILLAGER", 4
            ), "魔女在狼人出局后可隐身被验为好人，末期可每晚击杀两名玩家。"),

            new Board("b_444_gargoyle_grave_keeper", "444石像鬼&守墓人", 12, roles(
                    "GARGOYLE", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "GRAVE_KEEPER", 1,
                    "VILLAGER", 4
            ), "石像鬼单独行动，普狼全出局后才能杀人；守墓人每晚得知被放逐玩家是否为狼。"),

            new Board("b_444_gargoyle_pumpkin_super_grave", "444石像鬼&南瓜鬼&超级守墓人", 12, roles(
                    "GARGOYLE", 1,
                    "PUMPKIN_GHOST", 1,
                    "WEREWOLF", 2,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "SUPER_GRAVE_KEEPER", 1,
                    "VILLAGER", 4
            ), "南瓜鬼在石像鬼出局前验金水，之后查杀；超级守墓人可指定继承者。"),

            new Board("b_4431_cursed_fox", "4431咒狐", 12, roles(
                    "CURSED_FOX", 1,
                    "WEREWOLF", 4,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "IDIOT", 1,
                    "VILLAGER", 3
            ), "咒狐不死于狼刀，被预言家验则死；任一方达到获胜条件时咒狐存活则取代获胜。"),

            new Board("b_444_time_wolf_prince", "444蚀时狼妃&定序王子", 12, roles(
                    "TIME_ECLIPSE_WOLF_PRINCESS", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "GUARD", 1,
                    "ORDER_PRINCE", 1,
                    "VILLAGER", 4
            ), "蚀时狼妃封锁时间使技能反向作用；定序王子可逆转时空重投一次。"),

            new Board("b_444_mechanical_wolf_psychic", "444机通", 12, roles(
                    "MECHANICAL_WOLF", 1,
                    "WEREWOLF", 3,
                    "PSYCHIC", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "GUARD", 1,
                    "VILLAGER", 4
            ), "机械狼可学习场上玩家技能；通灵师可验出玩家具体身份。"),

            new Board("b_4431_war_wolf_socialite", "4431战狼&名媛", 12, roles(
                    "WAR_WOLF", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "SOCIALITE", 1,
                    "VILLAGER", 3,
                    "MIXED_BLOOD", 1
            ), "战狼免疫所有神职技能，只能被自刀/自爆/公投出局；名媛宠幸玩家，名媛死则被宠幸者也死。"),

            new Board("b_4431_poison_wolf_officer", "4431毒狼&命官", 12, roles(
                    "POISON_WOLF", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "DESTINY_OFFICER", 1,
                    "VILLAGER", 3,
                    "MIXED_BLOOD", 1
            ), "毒狼可封印一名玩家技能，全场一次；命官标记玩家，该玩家出局则命官随之死。"),

            new Board("b_444_tengu_moon_girl", "444天狗&月女", 12, roles(
                    "TENGU", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "MOON_GIRL", 1,
                    "VILLAGER", 4
            ), "天狗每晚可吞噬一人使其次日免疫放逐；月女可一次性日夜颠倒。"),

            new Board("b_444_grandma_wolf_red_hood", "444狼外婆&小红帽", 12, roles(
                    "GRANDMA_WOLF", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "LITTLE_RED_RIDING_HOOD", 1,
                    "VILLAGER", 4
            ), "狼外婆死后下一晚预言家验人结果颠倒，且狼人免疫夜间攻击；小红帽可转移死亡目标。"),

            new Board("b_444_chaos_deer", "444混沌之魔&灵鹿", 12, roles(
                    "CHAOS_DEMON", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "HUNTER", 1,
                    "JUDGE", 1,
                    "SPIRIT_DEER", 1,
                    "VILLAGER", 4
            ), "混沌之魔可置换两人死亡；审判官可召会裁决；灵鹿可创造平安夜并抵御一次伤害。"),

            new Board("b_444_demon_emperor_saint", "444帝尊魔皇&九天圣人", 12, roles(
                    "DEMON_EMPEROR", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "NINE_HEAVEN_SAINT", 1,
                    "VILLAGER", 4
            ), "帝尊魔皇不入狼窝，三狼出局后带刀；九天圣人两种状态分别免疫夜间/白天伤害。"),

            new Board("b_444_fox_fairy_yinyang", "444狐仙&阴阳使者", 12, roles(
                    "FOX_FAIRY", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "YIN_YANG_MESSENGER", 1,
                    "VILLAGER", 4
            ), "狐仙标记并夺魂投票者；阴阳使者可复活一名死亡玩家，复活狼人当局无法刀人。"),

            new Board("b_444_wolf_crow_claw_alchemist", "444狼鸦之爪&炼金魔女", 12, roles(
                    "WOLF_CROW_CLAW", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "ALCHEMY_WITCH", 1,
                    "DREAM_EATER", 1,
                    "IDIOT", 1,
                    "VILLAGER", 4
            ), "狼鸦之爪开局被封印，狼少于3人时解封获得无视保护的单独袭击；炼金魔女可限制狼刀范围。"),

            new Board("b_444_moon_spirit_wolf", "444月灵狼", 12, roles(
                    "MOON_SPIRIT_WOLF", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "GUARD", 1,
                    "VILLAGER", 3,
                    "ADVANCED_VILLAGER", 1
            ), "月灵狼两侧有神职则嗥叫，所有人可见；高级村民为明牌绝对好人。"),

            new Board("b_3441_vampire_knight", "3441吸血鬼骑士", 12, roles(
                    "WOLF_KING", 1,
                    "VAMPIRE_KNIGHT", 1,
                    "WEREWOLF", 2,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "IDIOT", 1,
                    "VILLAGER", 4
            ), "吸血鬼选傀儡，每晚吸噬一人；场上存活玩家全被吸噬且吸血鬼存活则第三方获胜。"),

            new Board("b_444_black_bat", "444黑蝙蝠", 12, roles(
                    "BLACK_BAT", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "GUARD", 1,
                    "VILLAGER", 4
            ), "黑蝙蝠庇护一名玩家，对被庇护者使用技能的玩家死亡，反弹一次后冷却。"),

            new Board("b_444_wolf_girl_fortune_teller", "444狼女&卜命人", 12, roles(
                    "WOLF_GIRL", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "FORTUNE_TELLER", 1,
                    "VILLAGER", 4
            ), "狼女首夜验金水，可一次颠倒预言家查验信息；卜命人预测死亡情况获取额外信息。"),

            new Board("b_444_snow_wolf_bard", "444雪狼&吟游者", 12, roles(
                    "SNOW_WOLF", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "BARD", 1,
                    "VILLAGER", 4
            ), "雪狼不会死在夜晚，被公投出局后令所有神下一晚无法使用技能；吟游者可改变被放逐目标。"),

            new Board("b_444_pure_white_night_shadow", "444纯白夜影", 12, roles(
                    "WOLF_WITCH", 1,
                    "WEREWOLF", 3,
                    "PURE_WHITE_GIRL", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "GUARD", 1,
                    "VILLAGER", 4
            ), "狼巫验到纯白之女则其死亡；纯白之女验到狼人则狼人死亡，双方验杀无视解药和守护。"),

            new Board("b_444_spirit_wolf", "444灵狼", 12, roles(
                    "SPIRIT_WOLF", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "GUARD", 1,
                    "VILLAGER", 4
            ), "灵狼首夜学习一名好人阵营玩家的技能，获得对应的特殊能力。"),

            new Board("b_444_wolf_witch_illusionist", "444狼巫&幻术师", 12, roles(
                    "WOLF_WITCH", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "ILLUSIONIST", 1,
                    "VILLAGER", 4
            ), "狼巫可每隔一晚诅咒一名好人令其技能失效；幻术师每隔一晚指定幻象代替自己死亡。"),

            new Board("b_246_plague_wolf", "246瘟疫狼", 12, roles(
                    "PLAGUE_WOLF", 2,
                    "SEER", 1,
                    "BEAR", 1,
                    "HUNTER", 1,
                    "IDIOT", 1,
                    "VILLAGER", 6
            ), "瘟疫狼可放弃猎杀改为感染一名玩家成为狼人，全场一次。"),

            new Board("b_444_christmas_wolf", "444圣诞老狼", 12, roles(
                    "CHRISTMAS_WOLF", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "IDIOT", 1,
                    "VILLAGER", 4
            ), "圣诞老狼被验金水，每晚可送礼物，好/坏礼物，打开坏礼物当场炸死。"),

            new Board("b_444_sleigh_white_wolf", "444雪橇白狼（衍生）", 12, roles(
                    "CHRISTMAS_WOLF", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "IDIOT", 1,
                    "VILLAGER", 3,
                    "SLEIGH_WHITE_WOLF", 1
            ), "雪橇白狼被验查杀但免疫功能狼效果，白天可翻牌开净化枪直接结束游戏。"),

            new Board("b_444_bomber_christmas_man", "444炸弹客&圣诞老人", 12, roles(
                    "BOMB_GUEST", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "SANTA_CLAUS", 1,
                    "VILLAGER", 4
            ), "炸弹客每晚送炸弹；圣诞老人可交换两人礼物，优先转移炸弹。"),

            new Board("b_444_nian_dragon_lion", "444年兽&舞龙舞狮", 12, roles(
                    "NIAN_BEAST", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "DRAGON_LION_DANCE", 1,
                    "VILLAGER", 4
            ), "年兽吞人免疫死亡；舞龙舞狮标记玩家可挡一次年兽，叫醒狼人可令年兽技能失效。"),

            new Board("b_444_awakened_night", "444觉醒之夜", 12, roles(
                    "AWAKENED_NOBLE", 1,
                    "WEREWOLF", 3,
                    "SEER", 1,
                    "WITCH", 1,
                    "HUNTER", 1,
                    "AWAKENED_FOOL", 1,
                    "VILLAGER", 4
            ), "夜之贵族可指定夜仆次日天亮出局；觉醒愚者白天被放逐时可免除出局并保护他人。")
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