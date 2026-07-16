package net.snowteb.warriorcats_events.network.packet.s2c.clan;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.WCEClient;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.client.ClanInfo;
import net.snowteb.warriorcats_events.client.ClientClanCache;
import net.snowteb.warriorcats_events.client.ClientPacketHandles;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCGenetics;
import net.snowteb.warriorcats_events.sound.ModSounds;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class S2CClanListPacket {

    public List<ClanInfo> clans;
    public boolean seeingMyClan;
    public boolean territoryMap;

    public S2CClanListPacket(List<ClanInfo> clans, boolean seeingMyClan, boolean territoryMap) {
        this.clans = clans;
        this.seeingMyClan = seeingMyClan;
        this.territoryMap = territoryMap;
    }

    public static int measure(S2CClanListPacket msg) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        encode(msg, buf);
        return buf.readableBytes();
    }

    public static void encode(S2CClanListPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.clans.size());
        for (ClanInfo info : msg.clans) {
            buf.writeUUID(info.uuid);
            buf.writeUtf(info.name);
            buf.writeInt(info.color);
            buf.writeUtf(info.leaderName);
            buf.writeUtf(info.clanSentence);
            buf.writeBoolean(info.canManage);
            buf.writeInt(info.memberCount);

            buf.writeInt(info.memberMorphNames.size());
            for (String morph : info.memberMorphNames) {
                buf.writeUtf(morph);
            }

            buf.writeInt(info.clanCats.size());
            for (ClanInfo.ClientClanCat cat : info.clanCats) {
                buf.writeUUID(cat.uuid);
                buf.writeUtf(cat.name);
                buf.writeUtf(cat.gender);
                buf.writeUtf(cat.rank);
                buf.writeUtf(cat.age);
                buf.writeInt(cat.variant);
                buf.writeUtf(cat.parents);

                buf.writeBoolean(cat.onGeneticalSkin);
                cat.genetics.encode(buf);
                cat.variants.encode(buf);
                cat.chimeraGenetics.encode(buf);
                cat.chimeraVariants.encode(buf);
            }

            buf.writeInt(info.clanLogs.size());
            for (ClanInfo.ClientLogEntry log : info.clanLogs) {
                buf.writeLong(log.gameTimeID);
                buf.writeComponent(log.message);
            }

            buf.writeInt(info.symbolIndex);

        }
        buf.writeBoolean(msg.seeingMyClan);
        buf.writeBoolean(msg.territoryMap);


    }


    public static S2CClanListPacket decode(FriendlyByteBuf buf) {
        int size = buf.readInt();
        List<ClanInfo> list = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            UUID uuid = buf.readUUID();
            String name = buf.readUtf();
            int color = buf.readInt();
            String leaderName = buf.readUtf();
            String clanSentence = buf.readUtf();
            boolean canManage = buf.readBoolean();
            int memberCount = buf.readInt();

            int morphSize = buf.readInt();
            List<String> morphNames = new ArrayList<>();
            for (int j = 0; j < morphSize; j++) {
                morphNames.add(buf.readUtf());
            }

            int catSize = buf.readInt();
            List<ClanInfo.ClientClanCat> cats = new ArrayList<>();

            for (int j = 0; j < catSize; j++) {
                UUID catUUID = buf.readUUID();
                String catName = buf.readUtf();
                String gender = buf.readUtf();
                String rank = buf.readUtf();
                String age = buf.readUtf();
                int variant = buf.readInt();
                String parents = buf.readUtf();

                boolean onGeneticalSkin = buf.readBoolean();
                WCGenetics genetics = WCGenetics.decode(buf);
                WCGenetics.GeneticalVariants variants = WCGenetics.GeneticalVariants.decode(buf);
                WCGenetics chimeraGens = WCGenetics.decode(buf);
                WCGenetics.GeneticalChimeraVariants chimeraVariants = WCGenetics.GeneticalChimeraVariants.decode(buf);

                cats.add(new ClanInfo.ClientClanCat(catUUID, catName, gender,
                        rank, age, variant, parents, onGeneticalSkin, genetics, chimeraGens, variants, chimeraVariants));
            }

            int logSize = buf.readInt();
            List<ClanInfo.ClientLogEntry> logs = new ArrayList<>();

            for (int j = 0; j < logSize; j++) {
                long time = buf.readLong();
                Component message = buf.readComponent();
                logs.add(new ClanInfo.ClientLogEntry(time, message));
            }

            int symbolIndex = buf.readInt();

            list.add(new ClanInfo(uuid, name, color, leaderName, clanSentence, canManage, memberCount, morphNames, cats, logs, symbolIndex));

        }
        boolean seeingMyClan = buf.readBoolean();
        boolean territoryMap = buf.readBoolean();



        return new S2CClanListPacket(list, seeingMyClan, territoryMap);
    }


    public static void handle(S2CClanListPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {


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

            ClientClanCache.setClans(msg.clans);
            WCEClient.playLocalSound(ModSounds.MENU_OPEN.get(), SoundSource.NEUTRAL, 0.8f,1.3f);

            ClientPacketHandles.openClanListScreen(msg.seeingMyClan, msg.territoryMap);
        });
        ctx.get().setPacketHandled(true);
    }

}

