package com.tracy.werewolf.model;

public enum Role {
    WEREWOLF("狼人", RoleCategory.WOLF),
    WOLF_KING("狼王", RoleCategory.WOLF),
    WHITE_WOLF_KING("白狼王", RoleCategory.WOLF),
    BEAUTY_WOLF("狼美人", RoleCategory.WOLF),
    GARGOYLE("石像鬼", RoleCategory.WOLF),
    MECHANICAL_WOLF("机械狼", RoleCategory.WOLF),
    HIDDEN_WOLF("隐狼", RoleCategory.WOLF),
    DEMON_KNIGHT("恶灵骑士", RoleCategory.WOLF),
    SNOW_WOLF("雪狼", RoleCategory.WOLF),
    THICK_SKIN_WOLF("厚皮狼", RoleCategory.WOLF),

    VILLAGER("平民", RoleCategory.GOOD),
    SEER("预言家", RoleCategory.GOOD),
    WITCH("女巫", RoleCategory.GOOD),
    HUNTER("猎人", RoleCategory.GOOD),
    GUARD("守卫", RoleCategory.GOOD),
    IDIOT("白痴", RoleCategory.GOOD),
    KNIGHT("骑士", RoleCategory.GOOD),
    GRAVE_KEEPER("守墓人", RoleCategory.GOOD),
    MAGICIAN("魔术师", RoleCategory.GOOD),
    DREAM_CATCHER("摄梦人", RoleCategory.GOOD),
    BEAR_TRAINER("驯熊师", RoleCategory.GOOD),
    FOX("狐狸", RoleCategory.GOOD),
    PSYCHIC("通灵师", RoleCategory.GOOD),
    EXORCIST("猎魔人", RoleCategory.GOOD),
    LITTLE_GIRL("小女孩", RoleCategory.GOOD),
    BOMB_EXPERT("炸弹师", RoleCategory.GOOD),
    ALCHEMIST("炼金魔女", RoleCategory.GOOD),
    PRIEST("祭司", RoleCategory.GOOD),
    MOON_PRIEST("月夜祭司", RoleCategory.GOOD),
    AWAKENED_HUNTER("觉醒猎人", RoleCategory.GOOD),
    AWAKENED_SEER("觉醒预言家", RoleCategory.GOOD),
    AWAKENED_WITCH("觉醒女巫", RoleCategory.GOOD),

    CUPID("丘比特", RoleCategory.THIRD_PARTY),
    THIEF("盗贼", RoleCategory.THIRD_PARTY),
    WILD_CHILD("野孩子", RoleCategory.THIRD_PARTY),
    MIXED_BLOOD("混血儿", RoleCategory.THIRD_PARTY),
    LOVERS("情侣", RoleCategory.THIRD_PARTY),
    SHADOW("影子", RoleCategory.THIRD_PARTY),
    PIPER("吹笛者", RoleCategory.THIRD_PARTY);

    private final String displayName;
    private final RoleCategory category;

    Role(String displayName, RoleCategory category) {
        this.displayName = displayName;
        this.category = category;
    }

    public String getDisplayName() {
        return displayName;
    }

    public RoleCategory getCategory() {
        return category;
    }
}
