package net.snowteb.warriorcats_events.battlemoves;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BattleMoveType <T extends BattleMove<T>> {


    private final Component name;
    private final Component description;
    private final BattleMoveCategory category;
    private final String id;
    private final ResourceLocation icon;
    private final BattleMove<T> move;

    public BattleMoveType(ResourceLocation id, BattleMoveCategory category, BattleMove<T> move) {
        this.name = Component.translatable("battlemove." + id.getNamespace() + "." + id.getPath());
        this.description = Component.translatable("battlemove." + id.getNamespace() + "." + id.getPath() + "." + "description");
        this.category = category;
        this.id = id.toString();
        this.icon = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "textures/battlemove/" + id.getPath() + ".png");
        this.move = move;
        this.move.setType(this);
    }

    public Component getName() {
        return name;
    }
    public Component getDescription() {
        return description;
    }

    public BattleMoveCategory getCategory() {
        return category;
    }

    public ResourceLocation getIcon() {
        return icon;
    }

    public String getId() {
        return id;
    }

    public BattleMove<T> getMove() {
        return move;
    }

    public record MoveAnimation(String animation, boolean loops) {
        public static MoveAnimation empty() {
            return new MoveAnimation("", false);
        }

        public boolean isEmpty() {
            return this.animation.isEmpty();
        }
    }

}
