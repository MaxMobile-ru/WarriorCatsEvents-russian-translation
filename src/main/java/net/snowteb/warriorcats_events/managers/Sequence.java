package net.snowteb.warriorcats_events.managers;

import net.minecraft.world.level.Level;
import net.snowteb.warriorcats_events.WarriorCatsEvents;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

/** Sequences by Klyonstar
 * This class allows to program sequences to run. Includes thenWait ticks,
 * <p>
 * running code every X ticks, or running code every Y ticks for X times, etc.
 * <p>
 * This class is under CC By 4.0 license
 * <a href="https://creativecommons.org/licenses/by/4.0/deed.es">...</a>
 * <p>
 * Might not be the greatest thing, but might as well be useful to someone one day.
 * <p>
 * Remember to include the SequenceManager.tick() in the Server or Client tick.
 */

public class Sequence {

    private final Queue<SequenceStep> steps = new LinkedList<>();
    private SequenceStep current;
    private final Level level;

    public Sequence(Level level) {
        this.level = level;
    }

    public Sequence then(Consumer<Level> r) {
        steps.add(level -> {
            run(r);

            return true;
        });
        return this;
    }

    public Sequence thenWait(int ticks) {
        steps.add(new SequenceStep() {
            int time = ticks;

            @Override
            public boolean tick(Level level) {
                time--;
                return time <= 0;
            }
        });
        return this;
    }

    public Sequence then(int ticks, Consumer<Level> r) {
        steps.add(new SequenceStep() {
            int time = ticks;
            @Override
            public boolean tick(Level level) {
                run(r);

                time--;
                return time <= 0;
            }
        });

        return this;
    }

    public Sequence thenEveryFor(int everyTicks, int forTicks, Consumer<Level> r) {
        steps.add(new SequenceStep() {
            int time = forTicks;
            int counter = 0;

            @Override
            public boolean tick(Level level) {
                counter++;

                if (counter % Math.max(1, everyTicks) == 0) {
                    run(r);
                }

                time--;
                return time <= 0;
            }
        });
        return this;
    }

    public Sequence thenEveryForTimes(int everyTicks, int times, Consumer<Level> r) {
        steps.add(new SequenceStep() {
            int remaining = times;
            int counter = 0;
            @Override
            public boolean tick(Level level) {
                counter++;

                if (counter % Math.max(1, everyTicks) == 0) {
                    run(r);
                    remaining--;
                }
                return remaining <= 0;
            }
        });
        return this;
    }

    public Sequence run() {
        SequenceManager.add(this);
        return this;
    }

    public void tick(Level level) {
        while (true) {
            if (current == null) {
                current = steps.poll();
                if (current == null) return;
            }

            if (!current.tick(level)) return;
            current = null;
        }
    }

    public boolean isFinished() {
        return current == null && steps.isEmpty();
    }

    private void run(Consumer<Level> r) {
        try {
            r.accept(this.level);
        } catch (Exception e) {
            WarriorCatsEvents.LOGGER.warn("A Sequence step has thrown an exception: ", e);
        }
    }

    public Level getLevel() {
        return this.level;
    }

    // Example of use
//    new Sequence(level)
//    .then(() -> {
//        level.sendParticles(ParticleTypes.SPLASH, player.getX(),player.getY() + 0.4, player.getZ(),
//                10, 0.3, 0.2, 0.3, 0.01);
//    })
//    .thenWait(40)
//    .thenEveryForTimes(20, 10, () -> {
//        level.sendParticles(ParticleTypes.SMALL_FLAME, player.getX(),player.getY() + 0.4,
//                player.getZ(), 10, 0.3, 0.2, 0.3,
//                0.01);
//    })
//    .thenWait(10)
//    .then(() -> {
//        level.playSound(null, player.blockPosition(), SoundEvents.CAT_HISS, SoundSource.PLAYERS, 0.2f ,1.5f);
//    })
//    .run();

}
