package net.snowteb.warriorcats_events.skills;

import net.snowteb.warriorcats_events.battlemoves.PlayerBattleMovesStore;

public interface ISkillData {

    int getSpeedLevel();
    void setSpeedLevel(int level);

    boolean isLeaping();
    void setLeaping(boolean isLeaping);

    int getLeapPower();
    void setLeapPower(int leapPower);

    int getHPLevel();
    void setHPLevel(int level);

    int getDMGLevel();
    void setDMGLevel(int level);

    int getJumpLevel();
    void setJumpLevel(int level);

    int getArmorLevel();
    void setArmorLevel(int level);

    void copyFrom(ISkillData other);

    boolean isClimbUnlocked();
    void setClimbUnlocked(boolean climbUnlocked);

    PlayerBattleMovesStore getBattleMovesStore();
}
