package net.snowteb.warriorcats_events.screen.menus;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.snowteb.warriorcats_events.WarriorCatsEvents;

public class ModMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, WarriorCatsEvents.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<StoneCleftMenu>> STONECLEFT_MENU =
            registerMenuType("stone_cleft_menu", StoneCleftMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<FreshKillPileMenu>> FRESHKILL_PILE_MENU =
            registerMenuType("fresh_kill_pile_menu", FreshKillPileMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<WCatMenu>> WCAT_INVENTORY =
            registerMenuType("wcat_inventory", WCatMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<HerbMixingMenu>> HERB_MIXING =
            registerMenuType("herb_mixing_menu", HerbMixingMenu::new);

    private static <T extends AbstractContainerMenu>
    DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
