package net.snowteb.warriorcats_events.integration;

import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Phantom;
import net.snowteb.warriorcats_events.entity.ModEntities;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import tocraft.walkers.ability.AbilityRegistry;
import tocraft.walkers.api.variant.TypeProviderRegistry;
import tocraft.walkers.integrations.AbstractIntegration;
import tocraft.walkers.traits.TraitRegistry;
import tocraft.walkers.traits.impl.FearedTrait;

/**
 * This class allows you to select different variants of Wild Cat to play as.
 */
public class WCatIntegration extends AbstractIntegration {

    @Override
    public void initialize() {

//        WalkersTickHandlers.register(ModEntities.WCAT.get(), new WalkersTickHandler<WCatEntity>() {
//            @Override
//            public void tick(Player player, WCatEntity entity) {
//
//            }
//        });

//        EntityUpdaters.register(ModEntities.WCAT.get(), new EntityUpdater<WCatEntity>() {
//            @Override
//            public void update(Player from, WCatEntity to) {
//                to.setPos(from.getX(), from.getY(), from.getZ());
//            }
//        });

    }

    @Override
    public void registerTypeProvider() {
        TypeProviderRegistry.register(ModEntities.WCAT.get(), new WCatTypeProvider());
        //TypeProviderRegistry.register(ModEntities.VANILLAWCAT.get(), new VanillaWCatTypeProvider());
        //TypeProviderRegistry.register(ModEntities.PLAYEABLECAT.get(), new PlayeableCatTypeProvider());
    }

    @Override
    public void registerAbilities() {
        AbilityRegistry.registerByClass(WCatEntity.class, new WCatAbility<>());
    }

    @Override
    public void registerTraits() {
        TraitRegistry.registerByClass(WCatEntity.class, (FearedTrait<WCatEntity>) FearedTrait.ofFearfulClass(Creeper.class));
        TraitRegistry.registerByClass(WCatEntity.class, (FearedTrait<WCatEntity>) FearedTrait.ofFearfulClass(Phantom.class));

    }

}
