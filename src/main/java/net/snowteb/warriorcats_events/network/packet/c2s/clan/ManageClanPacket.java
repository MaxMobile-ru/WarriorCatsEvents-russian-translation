package net.snowteb.warriorcats_events.network.packet.c2s.clan;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.clan.ClanObjects;
import net.snowteb.warriorcats_events.client.ClientClans;
import net.snowteb.warriorcats_events.network.ModPackets;
import net.snowteb.warriorcats_events.network.packet.s2c.clan.DisplayClanMenuMessage;
import net.snowteb.warriorcats_events.network.packet.s2c.clan.UpdateClanEntryPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class ManageClanPacket {

    private ManageClanPacket(Action action, UUID clanUUID) {
        this.action = action;
        this.clanUUID = clanUUID;
    }

    private enum Action {
        CHANGE_COLOR,
        SET_BIO,
        SET_LEADER_MESSAGE,
        SET_PUBLIC_LOGS,
        SET_ALLOW_REQUESTS,
        SET_PROTECT_TERRITORY,
        SET_PROTECT_CONTAINERS,
        NONE
    }

    private final Action action;
    private final UUID clanUUID;

    private int intValue = -1;
    private String stringValue = "";
    private boolean booleanValue = false;

    public static ManageClanPacket setClanColor(UUID clanUUID, int newColor) {
        ManageClanPacket packet = new ManageClanPacket(Action.CHANGE_COLOR, clanUUID);
        packet.intValue = newColor;
        return packet;
    }

    public static ManageClanPacket setClanBio(UUID clanUUID, String newBio) {
        ManageClanPacket packet = new ManageClanPacket(Action.SET_BIO, clanUUID);
        packet.stringValue = newBio;
        return packet;
    }

    public static ManageClanPacket setLeaderMessage(UUID clanUUID, String newLeaderMessage) {
        ManageClanPacket packet = new ManageClanPacket(Action.SET_LEADER_MESSAGE, clanUUID);
        packet.stringValue = newLeaderMessage;
        return packet;
    }

    public static ManageClanPacket setPublicLogs(UUID clanUUID, boolean v) {
        ManageClanPacket packet = new ManageClanPacket(Action.SET_PUBLIC_LOGS, clanUUID);
        packet.booleanValue = v;
        return packet;
    }

    public static ManageClanPacket setAllowRequests(UUID clanUUID, boolean v) {
        ManageClanPacket packet = new ManageClanPacket(Action.SET_ALLOW_REQUESTS, clanUUID);
        packet.booleanValue = v;
        return packet;
    }

    public static ManageClanPacket setProtectTerritory(UUID clanUUID, boolean v) {
        ManageClanPacket packet = new ManageClanPacket(Action.SET_PROTECT_TERRITORY, clanUUID);
        packet.booleanValue = v;
        return packet;
    }

    public static ManageClanPacket setProtectContainers(UUID clanUUID, boolean v) {
        ManageClanPacket packet = new ManageClanPacket(Action.SET_PROTECT_CONTAINERS, clanUUID);
        packet.booleanValue = v;
        return packet;
    }

    public static ManageClanPacket decode(FriendlyByteBuf buf) {
        ManageClanPacket packet = new ManageClanPacket(buf.readEnum(Action.class), buf.readUUID());

        switch (packet.action) {
            case CHANGE_COLOR -> {
                packet.intValue = buf.readInt();
            }
            case SET_BIO -> {
                packet.stringValue = buf.readUtf();
            }
            case SET_LEADER_MESSAGE -> {
                packet.stringValue = buf.readUtf();
            }
            case SET_PUBLIC_LOGS -> {
                packet.booleanValue = buf.readBoolean();
            }
            case SET_ALLOW_REQUESTS -> {
                packet.booleanValue = buf.readBoolean();
            }
            case SET_PROTECT_TERRITORY -> {
                packet.booleanValue = buf.readBoolean();
            }
            case SET_PROTECT_CONTAINERS -> {
                packet.booleanValue = buf.readBoolean();
            }
        }

        return packet;
    }

    public static void encode(ManageClanPacket packet, FriendlyByteBuf buf) {
        buf.writeEnum(packet.action);
        buf.writeUUID(packet.clanUUID);

        switch (packet.action) {
            case CHANGE_COLOR -> {
                buf.writeInt(packet.intValue);
            }
            case SET_BIO -> {
                buf.writeUtf(packet.stringValue, 140);
            }
            case SET_LEADER_MESSAGE -> {
                buf.writeUtf(packet.stringValue, 160);
            }
            case SET_PUBLIC_LOGS -> {
                buf.writeBoolean(packet.booleanValue);
            }
            case SET_ALLOW_REQUESTS -> {
                buf.writeBoolean(packet.booleanValue);
            }
            case SET_PROTECT_TERRITORY -> {
                buf.writeBoolean(packet.booleanValue);
            }
            case SET_PROTECT_CONTAINERS -> {
                buf.writeBoolean(packet.booleanValue);
            }
        }

    }

    public static void handle(ManageClanPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {

            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ServerLevel level = player.serverLevel().getServer().overworld();

            ClanData data = ClanData.get(level);
            ClanData.Clan clan = data.getClan(packet.clanUUID);
            if (clan == null) return;
            boolean canManage = data.canManage(clan, player.getUUID());
            if (!canManage) return;
            boolean isOwner = clan.members.get(player.getUUID()).getPermissions() == ClanData.ClanPermissions.OWNER;

            Component messageToDisplay = Component.empty();
            Component log = Component.empty();
            boolean shouldUpdate = false;
            boolean error = false;

            switch (packet.action) {
                case CHANGE_COLOR -> {
                    if (!isOwner) {
                        messageToDisplay = Component.translatable("clan.no_permissions");
                        error = true;
                        break;
                    }

                    clan.color = packet.intValue;
                    shouldUpdate = true;
                    messageToDisplay = Component.translatable("clan.update_successful");
                    log = Component.translatable("clan.color_changed_log", ClanData.logFormattedPlayerName(player));
                }
                case SET_BIO -> {
                    if (!isOwner) {
                        messageToDisplay = Component.translatable("clan.no_permissions");
                        error = true;
                        break;
                    }
                    if (clan.clanBioSentence.equals(packet.stringValue)) return;

                    clan.clanBioSentence = packet.stringValue;
                    shouldUpdate = true;
                    messageToDisplay = Component.translatable("clan.update_successful");
                    log = Component.translatable("clan.bio_changed_log", ClanData.logFormattedPlayerName(player));
                }
                case SET_LEADER_MESSAGE -> {
                    if (!isOwner) {
                        messageToDisplay = Component.translatable("clan.no_permissions");
                        error = true;
                        break;
                    }
                    if (clan.leaderClanMessage.equals(packet.stringValue)) return;

                    clan.leaderClanMessage = packet.stringValue;
                    shouldUpdate = true;
                    messageToDisplay = Component.translatable("clan.update_successful");
                    log = Component.translatable("clan.leader_message_changed_log", ClanData.logFormattedPlayerName(player));
                }
                case SET_PUBLIC_LOGS -> {
                    clan.publicClanLogs = packet.booleanValue;
                    shouldUpdate = true;
                    messageToDisplay = Component.translatable("clan.update_successful");
                    log = Component.translatable("clan.logs_visibility_changed", ClanData.logFormattedPlayerName(player), clan.publicClanLogs);
                }
                case SET_ALLOW_REQUESTS -> {
                    clan.allowJoinRequests = packet.booleanValue;
                    shouldUpdate = true;
                    messageToDisplay = Component.translatable("clan.update_successful");
                    log = Component.translatable("clan.allow_requests_changed", ClanData.logFormattedPlayerName(player), clan.allowJoinRequests);
                }
                case SET_PROTECT_TERRITORY -> {
                    if (!isOwner) {
                        messageToDisplay = Component.translatable("clan.no_permissions");
                        error = true;
                        break;
                    }

                    clan.protectTerritory = packet.booleanValue;
                    shouldUpdate = true;
                    messageToDisplay = Component.translatable("clan.update_successful");
                    log = Component.translatable("clan.protect_territory_log", ClanData.logFormattedPlayerName(player), clan.protectTerritory);
                }
                case SET_PROTECT_CONTAINERS -> {
                    if (!isOwner) {
                        messageToDisplay = Component.translatable("clan.no_permissions");
                        error = true;
                        break;
                    }

                    clan.protectContainers = packet.booleanValue;
                    shouldUpdate = true;
                    messageToDisplay = Component.translatable("clan.update_successful");
                    log = Component.translatable("clan.protect_containers_log", ClanData.logFormattedPlayerName(player), clan.protectContainers);
                }
            }

            if (!messageToDisplay.getString().isEmpty()) {
                ModPackets.sendToPlayer(new DisplayClanMenuMessage(messageToDisplay, error), player);
            }

            if (!log.getString().isEmpty()) {
                data.registerLog(level.getServer().overworld(), clan.clanUUID, log, ClanObjects.ClanLogEntry.Type.PLAYERS);
            }


            if (shouldUpdate) {
                data.updateScreen(clan, player);
            }

        });

        ctx.get().setPacketHandled(true);
    }
}

