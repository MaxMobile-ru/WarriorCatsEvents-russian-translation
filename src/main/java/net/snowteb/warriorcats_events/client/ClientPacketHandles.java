package net.snowteb.warriorcats_events.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.attachments.CapabilityManager;
import net.snowteb.warriorcats_events.attachments.ModAttachments;
import net.snowteb.warriorcats_events.attachments.WCEPlayerData;
import net.snowteb.warriorcats_events.diseases.Diseaseable;
import net.snowteb.warriorcats_events.entity.custom.EagleEntity;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCGenetics;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import net.snowteb.warriorcats_events.network.packet.s2c.clan.S2CClanListPacket;
import net.snowteb.warriorcats_events.screen.menus.SetPoseMenu;
import net.snowteb.warriorcats_events.screen.screens.*;
import net.snowteb.warriorcats_events.screen.screens.createmorph.CreateMorphScreen;

import java.util.List;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class ClientPacketHandles {

    public static void openCatScreen(int catId, boolean isDeputy) {

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        var entity = mc.level.getEntity(catId);

        if (entity instanceof WCatEntity cat) {
            mc.setScreen(new CatDataScreen(cat.getDisplayName(), cat, isDeputy));
        }
    }

    public static void openKitSpawnScreen(int entityID) {

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        WCatEntity kitten = (WCatEntity) mc.level.getEntity(entityID);
        if (kitten == null) return;

        mc.setScreen(new KitCreateScreen(kitten));
    }

    public static void openAncientStickScreen(List<Integer> entityIds, List<Integer> eagleIds) {
            Minecraft.getInstance().execute(() -> {
                Level level = Minecraft.getInstance().level;
                if (level == null) return;

                List<WCatEntity> cats = entityIds.stream()
                        .map(level::getEntity)
                        .filter(e -> e instanceof WCatEntity)
                        .map(e -> (WCatEntity) e)
                        .toList();

                List<EagleEntity> eagles = eagleIds.stream()
                        .map(level::getEntity)
                        .filter(e -> e instanceof EagleEntity)
                        .map(e -> (EagleEntity) e)
                        .toList();

                Minecraft.getInstance().setScreen(new AncientStickScreen(cats, eagles));
            });

    }

    public static void openClanCreateScreen(String morphName) {
        Minecraft.getInstance().execute(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return;

            mc.setScreen(new CreateClanScreen(morphName));
        });

    }

    public static void openClanListScreen(boolean seeingMyClan, boolean territoryMap) {
        Screen parent = new ClanListScreen(seeingMyClan);

        if (territoryMap) {
            Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(new TerritoryMapScreen(parent)));
            return;
        }

        Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(parent));
    }

    public static void openManageClanScreen(ClanInfo info) {
        Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(new ManageClanScreen(info)));
    }

    public static void openSpecificClan(String clanName, UUID clanUUID) {
        Minecraft.getInstance().execute(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return;

            mc.setScreen(new SpecificClanScreen(clanName, clanUUID));
        });

    }

    public static void openCreateMorphScreen(boolean isSummoning) {
        Minecraft.getInstance().execute(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return;

            mc.setScreen(new CreateMorphScreen(isSummoning));
        });

    }

    public static void openSaveMorphScreen(String key ,WCGenetics genetics, WCGenetics.GeneticalVariants variants,
                                           WCGenetics chimeraGenetics, WCGenetics.GeneticalChimeraVariants chimeraVariants,
                                           boolean onGeneticalSkin, int presetVariant) {
        Minecraft.getInstance().execute(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return;


            mc.setScreen(new SaveChatMorphScreen(key ,genetics, variants, chimeraGenetics, chimeraVariants, onGeneticalSkin, presetVariant));
        });

    }

    public static void openPatrolScreen(List<Integer> entityIds, UUID clanUUID, int deputyID, boolean deputy) {
        Minecraft.getInstance().execute(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return;

            List<WCatEntity> cats = entityIds.stream()
                    .map(mc.level::getEntity)
                    .filter(e -> e instanceof WCatEntity)
                    .map(e -> (WCatEntity) e)
                    .toList();

            mc.setScreen(new PatrolScreen(cats, clanUUID,  deputyID, deputy));
        });

    }


    public static void openPlayerCatScreen(WCEPlayerData.PackedData data, UUID targetUUID, int myKitCooldown, boolean editingProfile) {

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        mc.setScreen(new PlayerCatDataScreen(data, targetUUID, myKitCooldown, editingProfile));

    }

    public static void openPoseMenu() {
        Minecraft.getInstance().execute(() -> {
            Minecraft mc = Minecraft.getInstance();
            mc.setScreen(new SetPoseMenu());
        });
    }

    public static void setDiseaseData(CompoundTag tag, int id) {
        Entity target = Minecraft.getInstance().level.getEntity(id);
        if (target == null) return;
        if (target instanceof Diseaseable<?> diseaseable) {
            diseaseable.loadDiseasesNBT(tag);
        }
    }

    public static void openChangelogScreen() {
        Minecraft.getInstance().execute(() -> {
           Minecraft mc = Minecraft.getInstance();
           mc.setScreen(new WCEChangelogScreen(null));
        });
    }

    public static void openFishingScreen(BlockPos pos) {
        Minecraft.getInstance().execute(() -> {
            Minecraft.getInstance().setScreen(new FishingScreen(pos));
        });
    }

    public static void openClanSetupScreen() {
        Minecraft.getInstance().execute(() -> {
            Minecraft.getInstance().setScreen(new ClanSetupScreen());
        });
    }

    public static void syncStealth(boolean unlocked, boolean isStealthOn, boolean isSwitchOn) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        CapabilityManager.attachmentProvider(player, ModAttachments.PLAYER_STEALTH, cap -> {
            cap.setUnlocked(unlocked);
            cap.setStealthOn(isStealthOn);
            cap.setOn(isSwitchOn);
        });
    }

    public static void syncSkillData(int speedLevel, int hpLevel, int dmgLevel, int jumpLevel, int armorLevel, boolean climbUnlocked) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        CapabilityManager.attachmentProvider(player, ModAttachments.PLAYER_SKILL, data -> {
            data.setSpeedLevel(speedLevel);
            data.setHPLevel(hpLevel);
            data.setDMGLevel(dmgLevel);
            data.setJumpLevel(jumpLevel);
            data.setArmorLevel(armorLevel);
            data.setClimbUnlocked(climbUnlocked);
        });
    }

    public static void measurePacketSize(S2CClanListPacket msg) {
        if (Minecraft.getInstance().player != null) {
            LocalPlayer localPlayer = Minecraft.getInstance().player;
            if (WarriorCatsEvents.Collaborators.isOwner(localPlayer.getUUID())) {
                if (localPlayer.isSpectator()) {
                    int size = S2CClanListPacket.measure(msg);
                    String text = "Size: " + size/1000 + " kb";
                    localPlayer.sendSystemMessage(Component.literal(text));
                }
            }
        }
    }

    public static void openNametagScreen(ItemStack item, InteractionHand pUsedHand) {
        Minecraft.getInstance().execute(() -> {
            Minecraft mc = Minecraft.getInstance();
            mc.setScreen(new RenameTagMenu(item, pUsedHand));
        });
    }

}

