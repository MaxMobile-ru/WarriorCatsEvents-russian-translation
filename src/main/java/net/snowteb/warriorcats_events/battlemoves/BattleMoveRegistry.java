package net.snowteb.warriorcats_events.battlemoves;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.snowteb.warriorcats_events.WarriorCatsEvents;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BattleMoveRegistry {


    private static final Map<String, BattleMoveType<? extends BattleMove<?>>> BATTLE_MOVES = new LinkedHashMap<>();;

    public static <T extends BattleMove<T>> BattleMoveType<T> register(BattleMoveType<T> type) {
        if (BATTLE_MOVES.containsKey(type.getId())) {
            throw new RuntimeException(type.getId() + " is a duplicate.");
        }
        BATTLE_MOVES.put(type.getId(), type);
        return type;
    }

    public static BattleMoveType<? extends BattleMove<?>> getByID(String id) {
        return BATTLE_MOVES.get(id);
    }

    public static Map<String, BattleMoveType<? extends BattleMove<?>>> getList() {
        return BATTLE_MOVES;
    }

    public static void init() {
        List<ModFileScanData> scanDataList = ModList.get().getAllScanData();
        for (ModFileScanData scanData : scanDataList) {
            for (ModFileScanData.AnnotationData annotation : scanData.getAnnotations()) {
                if (annotation.annotationType().getClassName().equals(WCEBattleMoves.class.getName())) {
                    String className = annotation.clazz().getClassName();
                    try {
                        Class.forName(className);
                        WarriorCatsEvents.LOGGER.info("Loaded battle moves registry class {}", className);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

}
