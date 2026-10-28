package net.snowteb.warriorcats_events.managers;

import net.minecraft.world.level.Level;

public interface SequenceStep {
    boolean tick(Level level);
}
