package net.snowteb.warriorcats_events.screen.menus;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.snowteb.warriorcats_events.WarriorCatsEvents;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, WarriorCatsEvents.MODID);



    public static final RegistryObject<MenuType<StoneCleftMenu>> STONECLEFT_MENU =
            registerMenuType("stone_cleft_menu", StoneCleftMenu::new);

    public static final RegistryObject<MenuType<FreshKillPileMenu>> FRESHKILL_PILE_MENU =
            registerMenuType("fresh_kill_pile_menu", FreshKillPileMenu::new);


    public static final RegistryObject<MenuType<WCatMenu>> WCAT_INVENTORY =
            registerMenuType("wcat_inventory", WCatMenu::new);

    public static final RegistryObject<MenuType<HerbMixingMenu>> HERB_MIXING =
            registerMenuType("herb_mixing_menu", HerbMixingMenu::new);

    private static <T extends AbstractContainerMenu>RegistryObject<MenuType<T>>
    registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
