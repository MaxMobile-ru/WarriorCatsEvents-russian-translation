package net.snowteb.warriorcats_events.network.packet.s2c.clan;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.client.ClanInfo;
import net.snowteb.warriorcats_events.client.ClientPacketHandles;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCGenetics;

import java.util.*;

public class S2CManageClanPacket implements CustomPacketPayload {

    private final ClanInfo clan;

    public S2CManageClanPacket(ClanInfo clan) {
        this.clan = clan;
    }

    public static void encode(S2CManageClanPacket msg, FriendlyByteBuf buf) {
        ClanInfo info = msg.clan;

        buf.writeUUID(info.uuid);
        buf.writeUtf(info.name);
        buf.writeInt(info.color);
        buf.writeUtf(info.leaderName);
        buf.writeUtf(info.clanSentence);
        buf.writeInt(info.memberCount);

        buf.writeInt(info.playersInClan.size());

        for (ClanInfo.Member member : info.playersInClan.values()) {
            buf.writeUUID(member.getPlayerUUID());
            buf.writeUtf(member.getPlayerMorphName());
            buf.writeUtf(member.getRank());
            buf.writeUtf(member.getPerms());
            buf.writeUtf(member.getPlayerMorphAge());
            buf.writeBoolean(member.isPlayerOnline());

            member.getGenetics().encode(buf);
            member.getVariants().encode(buf);
            member.getChimeraGenetics().encode(buf);
            member.getChimeraVariants().encode(buf);
            buf.writeBoolean(member.isOnGeneticalSkin());
            buf.writeInt(member.getMorphVariant());
        }

        buf.writeInt(info.symbolIndex);

    }


    public static S2CManageClanPacket decode(FriendlyByteBuf buf) {
        UUID uuid = buf.readUUID();
        String name = buf.readUtf();
        int color = buf.readInt();
        String leaderName = buf.readUtf();
        String clanSentence = buf.readUtf();
        int memberCount = buf.readInt();

        int mapSize = buf.readInt();
        Map<UUID, ClanInfo.Member> playersInClan = new HashMap<>();
        for (int i = 0; i < mapSize; i++) {
            UUID memberUUID = buf.readUUID();
            String morphName = buf.readUtf();
            String rank = buf.readUtf();
            String perms = buf.readUtf();
            String age = buf.readUtf();
            boolean isOnline = buf.readBoolean();

            WCGenetics genetics = WCGenetics.decode(buf);
            WCGenetics.GeneticalVariants variants = WCGenetics.GeneticalVariants.decode(buf);
            WCGenetics chimeraGenetics = WCGenetics.decode(buf);
            WCGenetics.GeneticalChimeraVariants chimeraVariants = WCGenetics.GeneticalChimeraVariants.decode(buf);
            boolean onGeneticalSkin = buf.readBoolean();
            int morphVariant = buf.readInt();

            ClanInfo.Member member = new ClanInfo.Member(
                    memberUUID,
                    morphName,
                    rank,
                    perms,
                    age,
                    isOnline,
                    genetics,
                    variants,
                    chimeraGenetics,
                    chimeraVariants,
                    onGeneticalSkin,
                    morphVariant
            );
            playersInClan.put(memberUUID, member);
        }

        int symbolIndex = buf.readInt();

        ClanInfo info = new ClanInfo(
                uuid,
                name,
                color,
                leaderName,
                clanSentence,
                memberCount,
                playersInClan,
                symbolIndex
        );

        return new S2CManageClanPacket(info);
    }


    public static void handle(S2CManageClanPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ClientPacketHandles.openManageClanScreen(msg.clan);
        });
    }

    public static final Type<S2CManageClanPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "manage_clan"));

    public static final StreamCodec<FriendlyByteBuf, S2CManageClanPacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> encode(pkt, buf),
                    buf -> decode(buf)
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

