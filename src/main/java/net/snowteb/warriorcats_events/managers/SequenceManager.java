package net.snowteb.warriorcats_events.managers;

import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/** Sequences by Klyonstar
 * This class allows to program sequences to run. Includes wait ticks,
 * <p>
 * running code every X ticks, or running code every Y ticks for X times, etc.
 * <p>
 * This class is under CC By 4.0 license
 * <a href="https://creativecommons.org/licenses/by/4.0/deed.es">...</a>
 * <p>
 * Might not be the greatest thing, but might as well be useful to someone one day.
 * <p>
 * Remember to include the SequenceManager.tick() in the level tick.
 */


public class SequenceManager {

    private static final class Side {
        final List<Sequence> active = new ArrayList<>();
        final List<Sequence> pending = new ArrayList<>();

        void tick(Level level) {
            active.addAll(pending);
            pending.clear();
            active.removeIf(seq -> {
                if (seq.getLevel() != level) return false;
                seq.tick(level);
                return seq.isFinished();
            });
        }

        void clear(Level level) {
            active.removeIf(s -> s.getLevel() == level);
            pending.removeIf(s -> s.getLevel() == level);
        }
    }

    private static final Side SERVER = new Side();
    private static final Side CLIENT = new Side();

    public static void add(Sequence seq) {
        (seq.getLevel().isClientSide() ? CLIENT : SERVER).pending.add(seq);
    }

    public static void tick(Level level) {
        (level.isClientSide() ? CLIENT : SERVER).tick(level);
    }

    public static void clearLevel(Level level) {
        SERVER.clear(level);
        CLIENT.clear(level);
    }
}