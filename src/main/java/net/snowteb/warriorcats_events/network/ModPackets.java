package net.snowteb.warriorcats_events.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.network.packet.c2s.cats.*;
import net.snowteb.warriorcats_events.network.packet.c2s.clan.*;
import net.snowteb.warriorcats_events.network.packet.c2s.others.*;
import net.snowteb.warriorcats_events.network.packet.c2s.skilltree.*;
import net.snowteb.warriorcats_events.network.packet.s2c.cats.*;
import net.snowteb.warriorcats_events.network.packet.s2c.clan.*;
import net.snowteb.warriorcats_events.network.packet.s2c.others.*;
import net.snowteb.warriorcats_events.network.packet.s2c.skilltree.*;
import net.snowteb.warriorcats_events.network.packet.s2c.skilltree.*;

/**
 * All about this or any packets, just ask me personally, i aint explaining all that
 */

@EventBusSubscriber
public class ModPackets {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(WarriorCatsEvents.MOD_VERSION)
                .executesOn(HandlerThread.NETWORK);

        registrar.playToServer(
                CtSPlayCatSoundPacket.TYPE,
                CtSPlayCatSoundPacket.CODEC,
                (pkt, ctx) -> pkt.handle(ctx)
        );

        registrar.playToServer(
                WaterPacket.TYPE,
                WaterPacket.CODEC,
                (pkt, ctx) -> pkt.handle(ctx)
        );

        registrar.playToClient(
                ThirstDataSyncStCPacket.TYPE,
                ThirstDataSyncStCPacket.CODEC,
                (pkt, ctx) -> pkt.handle(ctx)
        );

        registrar.playToClient(
                SyncSkillDataPacket.TYPE,
                SyncSkillDataPacket.CODEC,
                (pkt, ctx) -> pkt.handle(ctx)
        );

        registrar.playToServer(
                CtSMoreSpeedPacket.TYPE,
                CtSMoreSpeedPacket.CODEC,
                (pkt, ctx) -> pkt.handle(ctx)
        );


        registrar.playToServer(
                CtSMoreArmorPacket.TYPE,
                CtSMoreArmorPacket.CODEC,
                (pkt, ctx) -> pkt.handle(ctx)
        );

        registrar.playToServer(
                CtSMoreHPPacket.TYPE,
                CtSMoreHPPacket.CODEC,
                (pkt, ctx) -> pkt.handle(ctx)
        );

        registrar.playToServer(
                CtSMoreJumpPacket.TYPE,
                CtSMoreJumpPacket.CODEC,
                (pkt, ctx) -> pkt.handle(ctx)
        );

        registrar.playToServer(
                CtSMoreDMGPacket.TYPE,
                CtSMoreDMGPacket.CODEC,
                (pkt, ctx) -> pkt.handle(ctx)
        );

        registrar.playToClient(
                StCFishingScreenPacket.TYPE,
                StCFishingScreenPacket.CODEC,
                (pkt, ctx) -> pkt.handle(ctx)
        );

        registrar.playToClient(
                StCStealthSyncPacket.TYPE,
                StCStealthSyncPacket.CODEC,
                (pkt, ctx) -> pkt.handle(ctx)
        );

        registrar.playToServer(
                CtSToggleStealthPacket.TYPE,
                CtSToggleStealthPacket.CODEC,
                (pkt, ctx) -> pkt.handle(ctx)
        );

        registrar.playToServer(
                ResetSkillsPacket.TYPE,
                ResetSkillsPacket.CODEC,
                (pkt, ctx) -> pkt.handle(ctx)
        );

        registrar.playToServer(
                ReqSkillDataPacket.TYPE,
                ReqSkillDataPacket.CODEC,
                (pkt, ctx) -> pkt.handle(ctx)
        );

        registrar.playToServer(
                SaveClanDataPacket.TYPE,
                SaveClanDataPacket.CODEC,
                (pkt, ctx) -> SaveClanDataPacket.handle(pkt, ctx)
        );

        registrar.playToServer(
                CtSSwitchStealthPacket.TYPE,
                CtSSwitchStealthPacket.CODEC,
                (pkt, ctx) -> CtSSwitchStealthPacket.handle(pkt, ctx)
        );

        registrar.playToServer(
                CtSSwitchShape.TYPE,
                CtSSwitchShape.CODEC,
                (pkt, ctx) -> pkt.handle(ctx)
        );

        registrar.playToServer(
                CtSFishFailed.TYPE,
                CtSFishFailed.CODEC,
                (pkt, ctx) -> CtSFishFailed.handle(pkt, ctx)
        );

        registrar.playToServer(
                CtSFishSuccesful.TYPE,
                CtSFishSuccesful.CODEC,
                (pkt, ctx) -> CtSFishSuccesful.handle(pkt, ctx)
        );

        registrar.playToServer(
                CtSUnlockStealthPacket.TYPE,
                CtSUnlockStealthPacket.CODEC,
                (pkt, ctx) -> CtSUnlockStealthPacket.handle(pkt, ctx)
        );

        registrar.playToClient(
                S2CSyncClanDataPacket.TYPE,
                S2CSyncClanDataPacket.CODEC,
                (pkt, ctx) -> S2CSyncClanDataPacket.handle(pkt, ctx)
        );

        registrar.playToClient(
                OpenClanSetupScreenPacket.TYPE,
                OpenClanSetupScreenPacket.CODEC,
                (pkt, ctx) -> pkt.handle(ctx)
        );

        registrar.playToServer(
                ReqMorphStatsPacket.TYPE,
                ReqMorphStatsPacket.CODEC,
                (pkt, ctx) -> ReqMorphStatsPacket.handle(pkt, ctx)
        );

        registrar.playToServer(
                SyncCatDataPacket.TYPE,
                SyncCatDataPacket.CODEC,
                (pkt, ctx) -> SyncCatDataPacket.handle(pkt, ctx)
        );

        registrar.playToServer(
                CatHomeActionsPacket.TYPE,
                CatHomeActionsPacket.CODEC,
                (pkt, ctx) -> CatHomeActionsPacket.handle(pkt, ctx)
        );

        registrar.playToServer(
                CatSetModePacket.TYPE,
                CatSetModePacket.CODEC,
                (pkt, ctx) -> CatSetModePacket.handle(pkt, ctx)
        );

        registrar.playToServer(
                RetrieveLastCatModePacket.TYPE,
                RetrieveLastCatModePacket.CODEC,
                (pkt, ctx) -> RetrieveLastCatModePacket.handle(pkt, ctx)
        );


        registrar.playToServer(
                PerformInteractionPacket.TYPE,
                PerformInteractionPacket.CODEC,
                (pkt, ctx) -> PerformInteractionPacket.handle(pkt, ctx)
        );

        registrar.playToServer(
                CtSTeleportToLocationPacket.TYPE,
                CtSTeleportToLocationPacket.CODEC,
                (pkt, ctx) -> CtSTeleportToLocationPacket.handle(pkt, ctx)
        );

        registrar.playToClient(
                StCKitCreateScreenPacket.TYPE,
                StCKitCreateScreenPacket.CODEC,
                (pkt, ctx) -> pkt.handle(ctx)
        );

        registrar.playToClient(
                OpenCatDataScreenPacket.TYPE,
                OpenCatDataScreenPacket.CODEC,
                (pkt, ctx) -> pkt.handle(ctx)
        );

        registrar.playToServer(
                UpdateClanDataPacket.TYPE,
                UpdateClanDataPacket.CODEC,
                (pkt, ctx) -> UpdateClanDataPacket.handle(pkt, ctx)
        );

        registrar.playToClient(
                SyncMorphStatsPacket.TYPE,
                SyncMorphStatsPacket.CODEC,
                (pkt, ctx) -> SyncMorphStatsPacket.handle(pkt, ctx)
        );

        registrar.playToServer(
                CtSPerformLeapPacket.TYPE,
                CtSPerformLeapPacket.CODEC,
                (pkt, ctx) -> CtSPerformLeapPacket.handle(pkt, ctx)
        );

        registrar.playToServer(
                KittingInteractionPacket.TYPE,
                KittingInteractionPacket.CODEC,
                (pkt, ctx) -> KittingInteractionPacket.handle(pkt, ctx)
        );

        registrar.playToClient(
                S2CManageClanPacket.TYPE,
                S2CManageClanPacket.CODEC,
                (pkt, ctx) -> S2CManageClanPacket.handle(pkt, ctx)
        );

        registrar.playToServer(
                C2SRegisterClanPacket.TYPE,
                C2SRegisterClanPacket.CODEC,
                (pkt, ctx) -> C2SRegisterClanPacket.handle(pkt, ctx)
        );

        registrar.playToClient(
                S2COpenRegisterClanScreenPacket.TYPE,
                S2COpenRegisterClanScreenPacket.CODEC,
                (pkt, ctx) -> S2COpenRegisterClanScreenPacket.handle(pkt, ctx)
        );

        registrar.playToClient(
                S2CClanListPacket.TYPE,
                S2CClanListPacket.CODEC,
                (pkt, ctx) -> S2CClanListPacket.handle(pkt, ctx)
        );

        registrar.playToServer(
                CommandCatsPacket.TYPE,
                CommandCatsPacket.CODEC,
                (pkt, ctx) -> pkt.handle(ctx)
        );

        registrar.playToClient(
                OpenAncientStickScreenPacket.TYPE,
                OpenAncientStickScreenPacket.CODEC,
                (pkt, ctx) -> OpenAncientStickScreenPacket.handle(pkt, ctx)
        );

        registrar.playToServer(
                SavePlayerGeneticsPacket.TYPE,
                SavePlayerGeneticsPacket.CODEC,
                (pkt, ctx) -> SavePlayerGeneticsPacket.handle(pkt, ctx)
        );

        registrar.playToServer(
                CtSRequestManageScreenPacket.TYPE,
                CtSRequestManageScreenPacket.CODEC,
                (pkt, ctx) -> CtSRequestManageScreenPacket.handle(pkt, ctx)
        );

        registrar.playToServer(
                CtSRegisterLogPacket.TYPE,
                CtSRegisterLogPacket.CODEC,
                (pkt, ctx) -> CtSRegisterLogPacket.handle(pkt, ctx)
        );

        registrar.playToServer(
                CtSManageClanMemberPacket.TYPE,
                CtSManageClanMemberPacket.CODEC,
                (pkt, ctx) -> CtSManageClanMemberPacket.handle(pkt, ctx)
        );

        registrar.playToClient(
                OpenSpecificClanScreen.TYPE,
                OpenSpecificClanScreen.CODEC,
                (pkt, ctx) -> OpenSpecificClanScreen.handle(pkt, ctx)
        );

        registrar.playToClient(
                OpenCreateMorphPacket.TYPE,
                OpenCreateMorphPacket.CODEC,
                (pkt, ctx) -> OpenCreateMorphPacket.handle(pkt, ctx)
        );

        registrar.playToServer(
                EmoteMorphPacket.TYPE,
                EmoteMorphPacket.CODEC,
                (pkt, ctx) -> EmoteMorphPacket.handle(pkt, ctx)
        );

        registrar.playToServer(
                CtSShareMorphToChat.TYPE,
                CtSShareMorphToChat.CODEC,
                (pkt, ctx) -> CtSShareMorphToChat.handle(pkt, ctx)
        );

        registrar.playToServer(
                CtSSaveChatMorphPacket.TYPE,
                CtSSaveChatMorphPacket.CODEC,
                (pkt, ctx) -> CtSSaveChatMorphPacket.handle(pkt, ctx)
        );

        registrar.playToClient(
                SyncTerritoryToClients.TYPE,
                SyncTerritoryToClients.CODEC,
                (pkt, ctx) -> SyncTerritoryToClients.handle(pkt, ctx)
        );

        registrar.playToServer(
                CallEaglesPacket.TYPE,
                CallEaglesPacket.CODEC,
                (pkt, ctx) -> CallEaglesPacket.handle(pkt, ctx)
        );

        registrar.playToServer(
                CtSDismountEaglePacket.TYPE,
                CtSDismountEaglePacket.CODEC,
                (pkt, ctx) -> pkt.handle(ctx)
        );

        registrar.playToClient(
                StCFinallySaveMorph.TYPE,
                StCFinallySaveMorph.CODEC,
                (pkt, ctx) -> StCFinallySaveMorph.handle(pkt, ctx)
        );

        registrar.playToServer(
                CtSClaimTerritory.TYPE,
                CtSClaimTerritory.CODEC,
                (pkt, ctx) -> CtSClaimTerritory.handle(pkt, ctx)
        );

        registrar.playToServer(
                CtSUnclaimTerritory.TYPE,
                CtSUnclaimTerritory.CODEC,
                (pkt, ctx) -> CtSUnclaimTerritory.handle(pkt, ctx)
        );

        registrar.playToServer(
                CtSSendPatrol.TYPE,
                CtSSendPatrol.CODEC,
                (pkt, ctx) -> CtSSendPatrol.handle(pkt, ctx)
        );

        registrar.playToServer(
                CtSRequestPatrolData.TYPE,
                CtSRequestPatrolData.CODEC,
                (pkt, ctx) -> CtSRequestPatrolData.handle(pkt, ctx)
        );

        registrar.playToClient(
                OpenPlayerCatDataScreenPacket.TYPE,
                OpenPlayerCatDataScreenPacket.CODEC,
                (pkt, ctx) -> pkt.handle(ctx)
        );

        registrar.playToServer(
                CompareVersionsPacket.TYPE,
                CompareVersionsPacket.CODEC,
                (pkt, ctx) -> CompareVersionsPacket.handle(pkt, ctx)
        );

        registrar.playToClient(
                StCOpenPatrolScreenPacket.TYPE,
                StCOpenPatrolScreenPacket.CODEC,
                (pkt, ctx) -> StCOpenPatrolScreenPacket.handle(pkt, ctx)
        );

        registrar.playToClient(
                OpenPoseMenuPacket.TYPE,
                OpenPoseMenuPacket.CODEC,
                (pkt, ctx) -> pkt.handle(ctx)
        );

        registrar.playToServer(
                SetPosePacket.TYPE,
                SetPosePacket.CODEC,
                (pkt, ctx) -> SetPosePacket.handle(pkt, ctx)
        );

        registrar.playToServer(
                SummonCustomCatPacket.TYPE,
                SummonCustomCatPacket.CODEC,
                (pkt, ctx) -> SummonCustomCatPacket.handle(pkt, ctx)
        );

        registrar.playToServer(
                PlayerKitPacket.TYPE,
                PlayerKitPacket.CODEC,
                (pkt, ctx) -> PlayerKitPacket.handle(pkt, ctx)
        );

        registrar.playToServer(
                EditProfilePacket.TYPE,
                EditProfilePacket.CODEC,
                (pkt, ctx) -> EditProfilePacket.handle(pkt, ctx)
        );

        registrar.playToServer(
                RenameClanPacket.TYPE,
                RenameClanPacket.CODEC,
                (pkt, ctx) -> RenameClanPacket.handle(pkt, ctx)
        );

        registrar.playToServer(
                CtSClimbPacket.TYPE,
                CtSClimbPacket.CODEC,
                (pkt, ctx) -> pkt.handle(ctx)
        );

        registrar.playToServer(
                CtSUnlockClimbPacket.TYPE,
                CtSUnlockClimbPacket.CODEC,
                (pkt, ctx) -> CtSUnlockClimbPacket.handle(pkt, ctx)
        );

        registrar.playToServer(
                CtSNameKitPacket.TYPE,
                CtSNameKitPacket.CODEC,
                (pkt, ctx) -> pkt.handle(ctx)
        );

        registrar.playToServer(
                CtSTakeCatPacket.TYPE,
                CtSTakeCatPacket.CODEC,
                (pkt, ctx) -> CtSTakeCatPacket.handle(pkt, ctx)
        );

        registrar.playToClient(
                SyncExhaustionPacket.TYPE,
                SyncExhaustionPacket.CODEC,
                (pkt, ctx) -> pkt.handle(ctx)
        );

        registrar.playToClient(
                SyncDiseasesPacket.TYPE,
                SyncDiseasesPacket.CODEC,
                (pkt, ctx) -> pkt.handle(ctx)
        );

        registrar.playToClient(
                OpenChangelogScreenPacket.TYPE,
                OpenChangelogScreenPacket.CODEC,
                (pkt, ctx) -> pkt.handle(ctx)
        );

        registrar.playToServer(
                RenameNametagPacket.TYPE,
                RenameNametagPacket.CODEC,
                (pkt, ctx) -> RenameNametagPacket.handle(pkt, ctx)
        );

        registrar.playToServer(
                KickNPCPacket.TYPE,
                KickNPCPacket.CODEC,
                (pkt, ctx) -> KickNPCPacket.handle(pkt, ctx)
        );


    }

    public static void sendToServer(CustomPacketPayload message) {
        PacketDistributor.sendToServer(message);
    }

    public static void sendToPlayer(CustomPacketPayload message, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, message);
    }

}
