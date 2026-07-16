//package net.snowteb.warriorcats_events.network.packet.c2s.others;
//
//import net.minecraft.network.FriendlyByteBuf;
//import net.minecraft.network.chat.Component;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.entity.MobSpawnType;
//import net.minecraft.world.level.Level;
//import net.minecraftforge.network.NetworkEvent;
//import net.snowteb.warriorcats_events.clan.ClanData;
//import net.snowteb.warriorcats_events.clan.WCEPlayerData;
//import net.snowteb.warriorcats_events.clan.WCEPlayerDataProvider;
//import net.snowteb.warriorcats_events.diseases.DiseaseManager;
//import net.snowteb.warriorcats_events.entity.ModEntities;
//import net.snowteb.warriorcats_events.entity.custom.wcat.WCGenetics;
//import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
//import tocraft.walkers.api.PlayerShape;
//
//import java.util.UUID;
//import java.util.function.Supplier;
//
//public class CtSSwapCatPacket {
//
//    private final int entityID;
//
//    public CtSSwapCatPacket(int entityID) {
//        this.entityID = entityID;
//    }
//
//    public CtSSwapCatPacket(FriendlyByteBuf buf) {
//        this.entityID = buf.readInt();
//    }
//
//    public void toBytes(FriendlyByteBuf buf) {
//        buf.writeInt(entityID);
//    }
//
//    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
//        ctx.get().enqueueWork(() -> {
//
//            ServerPlayer player = ctx.get().getSender();
//            if (player == null) return;
//
//            Entity entity = player.serverLevel().getEntity(entityID);
//            if (entity == null) return;
//            if (!(entity instanceof WCatEntity cat)) return;
//
//            LivingEntity current = PlayerShape.getCurrentShape(player);
//
//            if (current instanceof WCatEntity catShape) {
//                spawnShape(catShape, player.serverLevel(), player);
//
//                newShape(cat, player);
//            } else {
//                newShape(cat, player);
//            }
//
//            DiseaseManager.refreshData(player);
//
//
//        });
//        ctx.get().setPacketHandled(true);
//        return true;
//    }
//
//    public void spawnShape(WCatEntity catShape, Level level, ServerPlayer player) {
//
//        WCatEntity cat = ModEntities.WCAT.get().create(level);
//        if (cat == null) return;
//
//        cat.setVariant(catShape.getVariant());
//
//        cat.setAge(catShape.getAge());
//        cat.setBaby(catShape.isBaby());
//        cat.setAppScale(cat.isAppScale());
//
//        cat.setPlayerBoundUuid(ClanData.EMPTY_UUID);
//
//        cat.setOnGround(true);
//
//        cat.teleportTo(player.getX(), player.getY(), player.getZ());
//        cat.setYBodyRot(player.getYRot());
//        cat.setYHeadRot(player.getYRot());
//        cat.setPos(player.position());
//        cat.setUUID(UUID.randomUUID());
//
//
//        cat.setOnGeneticalSkin(catShape.isOnGeneticalSkin());
//        cat.setGenetics(catShape.getGenetics());
//        cat.setGeneticalVariants(catShape.getGenVariants());
//        cat.setChimeraGenetics(catShape.getChimeraGenetics());
//        cat.setGeneticalVariantsChimera(catShape.getChimeraGenVariants());
//        cat.setCustomName(catShape.getCustomName());
//
//        level.addFreshEntity(cat);
//
//    }
//
//    public void newShape(WCatEntity catShape, ServerPlayer player) {
//
//        WCatEntity cat = ModEntities.WCAT.get().create(player.serverLevel());
//        if (cat == null) return;
//
//        player.teleportTo(catShape.getX(), catShape.getY(), catShape.getZ());
//
//        cat.finalizeSpawn(player.serverLevel(), player.serverLevel().getCurrentDifficultyAt(player.blockPosition()),
//                MobSpawnType.MOB_SUMMONED, null, null);
//
//        cat.teleportTo(player.getX(), player.getY(), player.getZ());
//        cat.setYBodyRot(player.getYRot());
//        cat.setYHeadRot(player.getYRot());
//        cat.setPos(player.position());
//        cat.setUUID(UUID.randomUUID());
//
//        cat.setOnGeneticalSkin(catShape.isOnGeneticalSkin());
//        cat.setGenetics(catShape.getGenetics());
//        cat.setGeneticalVariants(catShape.getGenVariants());
//        cat.setChimeraGenetics(catShape.getChimeraGenetics());
//        cat.setGeneticalVariantsChimera(catShape.getChimeraGenVariants());
//        cat.setCustomName(catShape.getCustomName());
//
//        cat.setPlayerBoundUuid(player.getUUID());
//        cat.setCustomNameVisible(true);
//        cat.setShowMorphName(true);
//
//        PlayerShape.updateShapes(player, null);
//
//        PlayerShape.updateShapes(player, cat);
//
//        catShape.discard();
//
//    }
//}
