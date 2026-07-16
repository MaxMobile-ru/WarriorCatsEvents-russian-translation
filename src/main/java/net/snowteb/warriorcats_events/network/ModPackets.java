package net.snowteb.warriorcats_events.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.network.packet.c2s.cats.*;
import net.snowteb.warriorcats_events.network.packet.c2s.clan.*;
import net.snowteb.warriorcats_events.network.packet.c2s.others.*;
import net.snowteb.warriorcats_events.network.packet.c2s.skilltree.*;
import net.snowteb.warriorcats_events.network.packet.s2c.cats.*;
import net.snowteb.warriorcats_events.network.packet.s2c.clan.*;
import net.snowteb.warriorcats_events.network.packet.s2c.others.*;
import net.snowteb.warriorcats_events.network.packet.s2c.skilltree.StCStealthSyncPacket;
import net.snowteb.warriorcats_events.network.packet.s2c.skilltree.SyncSkillDataPacket;

/**
 * All about this or any packets, just ask me personally, i aint explaining all that
 */
public class ModPackets {
    public static SimpleChannel INSTANCE;
    private static int packetID = 0;
    private static int id() {
        return packetID++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(ResourceLocation.fromNamespaceAndPath(WarriorCatsEvents.MODID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();


        INSTANCE = net;
// 0
        net.messageBuilder(CtSPlayCatSoundPacket.class, 0, NetworkDirection.PLAY_TO_SERVER)
                .decoder(CtSPlayCatSoundPacket::new)
                .encoder(CtSPlayCatSoundPacket::toBytes)
                .consumerMainThread(CtSPlayCatSoundPacket::handle)
                .add();
        net.messageBuilder(WaterPacket.class, 1, NetworkDirection.PLAY_TO_SERVER)
                .decoder(WaterPacket::new)
                .encoder(WaterPacket::toBytes)
                .consumerMainThread(WaterPacket::handle)
                .add();
        net.messageBuilder(ThirstDataSyncStCPacket.class, 2, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ThirstDataSyncStCPacket::new)
                .encoder(ThirstDataSyncStCPacket::toBytes)
                .consumerMainThread(ThirstDataSyncStCPacket::handle)
                .add();

        net.messageBuilder(CtSMoreSpeedPacket.class, 3, NetworkDirection.PLAY_TO_SERVER)
                .decoder(CtSMoreSpeedPacket::new)
                .encoder(CtSMoreSpeedPacket::toBytes)
                .consumerMainThread(CtSMoreSpeedPacket::handle)
                .add();
        net.messageBuilder(CtSMoreHPPacket.class, 4, NetworkDirection.PLAY_TO_SERVER)
                .decoder(CtSMoreHPPacket::new)
                .encoder(CtSMoreHPPacket::toBytes)
                .consumerMainThread(CtSMoreHPPacket::handle)
                .add();
        net.messageBuilder(CtSMoreDMGPacket.class, 5, NetworkDirection.PLAY_TO_SERVER)
                .decoder(CtSMoreDMGPacket::new)
                .encoder(CtSMoreDMGPacket::toBytes)
                .consumerMainThread(CtSMoreDMGPacket::handle)
                .add();
        net.messageBuilder(CtSMoreJumpPacket.class, 6, NetworkDirection.PLAY_TO_SERVER)
                .decoder(CtSMoreJumpPacket::new)
                .encoder(CtSMoreJumpPacket::toBytes)
                .consumerMainThread(CtSMoreJumpPacket::handle)
                .add();
        net.messageBuilder(CtSMoreArmorPacket.class, 7, NetworkDirection.PLAY_TO_SERVER)
                .decoder(CtSMoreArmorPacket::new)
                .encoder(CtSMoreArmorPacket::toBytes)
                .consumerMainThread(CtSMoreArmorPacket::handle)
                .add();

        net.messageBuilder(SyncSkillDataPacket.class, 8, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncSkillDataPacket::new)
                .encoder(SyncSkillDataPacket::toBytes)
                .consumerMainThread(SyncSkillDataPacket::handle)
                .add();
        net.messageBuilder(ReqSkillDataPacket.class, 9, NetworkDirection.PLAY_TO_SERVER)
                .decoder(ReqSkillDataPacket::new)
                .encoder(ReqSkillDataPacket::toBytes)
                .consumerMainThread(ReqSkillDataPacket::handle)
                .add();

        //10
        net.messageBuilder(ResetSkillsPacket.class, 10, NetworkDirection.PLAY_TO_SERVER)
                .decoder(ResetSkillsPacket::new)
                .encoder(ResetSkillsPacket::toBytes)
                .consumerMainThread(ResetSkillsPacket::handle)
                .add();

        net.messageBuilder(CtSToggleStealthPacket.class, 11, NetworkDirection.PLAY_TO_SERVER)
                .decoder(CtSToggleStealthPacket::new)
                .encoder(CtSToggleStealthPacket::toBytes)
                .consumerMainThread(CtSToggleStealthPacket::handle)
                .add();

        net.messageBuilder(StCStealthSyncPacket.class, 12, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(StCStealthSyncPacket::new)
                .encoder(StCStealthSyncPacket::toBytes)
                .consumerMainThread(StCStealthSyncPacket::handle)
                .add();

        net.messageBuilder(StCFishingScreenPacket.class, 13, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(StCFishingScreenPacket::new)
                .encoder(StCFishingScreenPacket::toBytes)
                .consumerMainThread(StCFishingScreenPacket::handle)
                .add();

        net.messageBuilder(CtSUnlockStealthPacket.class, 14, NetworkDirection.PLAY_TO_SERVER)
                .encoder(CtSUnlockStealthPacket::encode)
                .decoder(CtSUnlockStealthPacket::decode)
                .consumerMainThread(CtSUnlockStealthPacket::handle)
                .add();

        net.messageBuilder(CtSFishSuccesful.class, 15, NetworkDirection.PLAY_TO_SERVER)
                .encoder(CtSFishSuccesful::encode)
                .decoder(CtSFishSuccesful::decode)
                .consumerMainThread(CtSFishSuccesful::handle)
                .add();

        net.messageBuilder(CtSFishFailed.class, 16, NetworkDirection.PLAY_TO_SERVER)
                .encoder(CtSFishFailed::encode)
                .decoder(CtSFishFailed::decode)
                .consumerMainThread(CtSFishFailed::handle)
                .add();

        net.messageBuilder(CtSSwitchShape.class, 17, NetworkDirection.PLAY_TO_SERVER)
                .decoder(CtSSwitchShape::new)
                .encoder(CtSSwitchShape::toBytes)
                .consumerMainThread(CtSSwitchShape::handle)
                .add();

        net.messageBuilder(CtSSwitchStealthPacket.class, 18, NetworkDirection.PLAY_TO_SERVER)
                .encoder(CtSSwitchStealthPacket::encode)
                .decoder(CtSSwitchStealthPacket::decode)
                .consumerMainThread(CtSSwitchStealthPacket::handle)
                .add();

        net.messageBuilder(SaveClanDataPacket.class, 19, NetworkDirection.PLAY_TO_SERVER)
                .encoder(SaveClanDataPacket::encode)
                .decoder(SaveClanDataPacket::decode)
                .consumerMainThread(SaveClanDataPacket::handle)
                .add();

        //20
        net.messageBuilder(OpenClanSetupScreenPacket.class, 20, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(OpenClanSetupScreenPacket::new)
                .encoder(OpenClanSetupScreenPacket::toBytes)
                .consumerMainThread(OpenClanSetupScreenPacket::handle)
                .add();

        net.messageBuilder(C2SSetVariantPacket.class, 21, NetworkDirection.PLAY_TO_SERVER)
                .encoder(C2SSetVariantPacket::encode)
                .decoder(C2SSetVariantPacket::decode)
                .consumerMainThread(C2SSetVariantPacket::handle)
                .add();

        net.messageBuilder(S2CSyncClanDataPacket.class, 22, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(S2CSyncClanDataPacket::encode)
                .decoder(S2CSyncClanDataPacket::decode)
                .consumerMainThread(S2CSyncClanDataPacket::handle)
                .add();

        net.messageBuilder(CtSTeleportToLocationPacket.class, 23, NetworkDirection.PLAY_TO_SERVER)
                .encoder(CtSTeleportToLocationPacket::encode)
                .decoder(CtSTeleportToLocationPacket::decode)
                .consumerMainThread(CtSTeleportToLocationPacket::handle)
                .add();

//        net.messageBuilder(PerformInteractionTalkPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
//                .encoder(PerformInteractionTalkPacket::encode)
//                .decoder(PerformInteractionTalkPacket::decode)
//                .consumerMainThread(PerformInteractionTalkPacket::handle)
//                .add();
//
//        net.messageBuilder(PerformInteractionGivePreyPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
//                .encoder(PerformInteractionGivePreyPacket::encode)
//                .decoder(PerformInteractionGivePreyPacket::decode)
//                .consumerMainThread(PerformInteractionGivePreyPacket::handle)
//                .add();
//
//        net.messageBuilder(PerformInteractionShowAffectionPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
//                .encoder(PerformInteractionShowAffectionPacket::encode)
//                .decoder(PerformInteractionShowAffectionPacket::decode)
//                .consumerMainThread(PerformInteractionShowAffectionPacket::handle)
//                .add();

        net.messageBuilder(PerformInteractionPacket.class, 24, NetworkDirection.PLAY_TO_SERVER)
                .encoder(PerformInteractionPacket::encode)
                .decoder(PerformInteractionPacket::decode)
                .consumerMainThread(PerformInteractionPacket::handle)
                .add();

        net.messageBuilder(RetrieveLastCatModePacket.class, 25, NetworkDirection.PLAY_TO_SERVER)
                .encoder(RetrieveLastCatModePacket::encode)
                .decoder(RetrieveLastCatModePacket::decode)
                .consumerMainThread(RetrieveLastCatModePacket::handle)
                .add();

        net.messageBuilder(CatSetModePacket.class, 26, NetworkDirection.PLAY_TO_SERVER)
                .encoder(CatSetModePacket::encode)
                .decoder(CatSetModePacket::decode)
                .consumerMainThread(CatSetModePacket::handle)
                .add();

        net.messageBuilder(CatHomeActionsPacket.class, 27, NetworkDirection.PLAY_TO_SERVER)
                .encoder(CatHomeActionsPacket::encode)
                .decoder(CatHomeActionsPacket::decode)
                .consumerMainThread(CatHomeActionsPacket::handle)
                .add();

        net.messageBuilder(SyncCatDataPacket.class, 28, NetworkDirection.PLAY_TO_SERVER)
                .encoder(SyncCatDataPacket::encode)
                .decoder(SyncCatDataPacket::decode)
                .consumerMainThread(SyncCatDataPacket::handle)
                .add();

        net.messageBuilder(ReqMorphStatsPacket.class, 29, NetworkDirection.PLAY_TO_SERVER)
                .decoder(ReqMorphStatsPacket::new)
                .encoder(ReqMorphStatsPacket::toBytes)
                .consumerMainThread(ReqMorphStatsPacket::handle)
                .add();

        //30
        net.messageBuilder(SyncMorphStatsPacket.class, 30, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SyncMorphStatsPacket::toBytes)
                .decoder(SyncMorphStatsPacket::new)
                .consumerMainThread(SyncMorphStatsPacket::handle)
                .add();

        net.messageBuilder(UpdateClanDataPacket.class, 31, NetworkDirection.PLAY_TO_SERVER)
                .decoder(UpdateClanDataPacket::decode)
                .encoder(UpdateClanDataPacket::encode)
                .consumerMainThread(UpdateClanDataPacket::handle)
                .add();

        net.messageBuilder(OpenCatDataScreenPacket.class, 32, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(OpenCatDataScreenPacket::new)
                .encoder(OpenCatDataScreenPacket::toBytes)
                .consumerMainThread(OpenCatDataScreenPacket::handle)
                .add();

        net.messageBuilder(StCKitCreateScreenPacket.class, 33, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(StCKitCreateScreenPacket::new)
                .encoder(StCKitCreateScreenPacket::toBytes)
                .consumerMainThread(StCKitCreateScreenPacket::handle)
                .add();

        net.messageBuilder(CtSCreateAndSpawnKitPacket.class, 34, NetworkDirection.PLAY_TO_SERVER)
                .decoder(CtSCreateAndSpawnKitPacket::new)
                .encoder(CtSCreateAndSpawnKitPacket::toBytes)
                .consumerMainThread(CtSCreateAndSpawnKitPacket::handle)
                .add();

        net.messageBuilder(KittingInteractionPacket.class, 35, NetworkDirection.PLAY_TO_SERVER)
                .decoder(KittingInteractionPacket::decode)
                .encoder(KittingInteractionPacket::encode)
                .consumerMainThread(KittingInteractionPacket::handle)
                .add();

        net.messageBuilder(CtSPerformLeapPacket.class, 36, NetworkDirection.PLAY_TO_SERVER)
                .decoder(CtSPerformLeapPacket::decode)
                .encoder(CtSPerformLeapPacket::encode)
                .consumerMainThread(CtSPerformLeapPacket::handle)
                .add();

        net.messageBuilder(OpenAncientStickScreenPacket.class, 37, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(OpenAncientStickScreenPacket::decode)
                .encoder(OpenAncientStickScreenPacket::encode)
                .consumerMainThread(OpenAncientStickScreenPacket::handle)
                .add();

        net.messageBuilder(CommandCatsPacket.class, 38, NetworkDirection.PLAY_TO_SERVER)
                .decoder(CommandCatsPacket::decode)
                .encoder(CommandCatsPacket::encode)
                .consumerMainThread(CommandCatsPacket::handle)
                .add();

        net.messageBuilder(S2CClanListPacket.class, 39, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(S2CClanListPacket::decode)
                .encoder(S2CClanListPacket::encode)
                .consumerMainThread(S2CClanListPacket::handle)
                .add();

        net.messageBuilder(C2SRegisterClanPacket.class, 40, NetworkDirection.PLAY_TO_SERVER)
                .decoder(C2SRegisterClanPacket::decode)
                .encoder(C2SRegisterClanPacket::encode)
                .consumerMainThread(C2SRegisterClanPacket::handle)
                .add();

        net.messageBuilder(S2COpenRegisterClanScreenPacket.class, 41, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(S2COpenRegisterClanScreenPacket::decode)
                .encoder(S2COpenRegisterClanScreenPacket::encode)
                .consumerMainThread(S2COpenRegisterClanScreenPacket::handle)
                .add();

        net.messageBuilder(S2CManageClanPacket.class, 42, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(S2CManageClanPacket::decode)
                .encoder(S2CManageClanPacket::encode)
                .consumerMainThread(S2CManageClanPacket::handle)
                .add();

        net.messageBuilder(CtSManageClanMemberPacket.class, 43, NetworkDirection.PLAY_TO_SERVER)
                .decoder(CtSManageClanMemberPacket::decode)
                .encoder(CtSManageClanMemberPacket::encode)
                .consumerMainThread(CtSManageClanMemberPacket::handle)
                .add();

        net.messageBuilder(CtSRegisterLogPacket.class, 44, NetworkDirection.PLAY_TO_SERVER)
                .decoder(CtSRegisterLogPacket::decode)
                .encoder(CtSRegisterLogPacket::encode)
                .consumerMainThread(CtSRegisterLogPacket::handle)
                .add();

        net.messageBuilder(CtSRequestManageScreenPacket.class, 45, NetworkDirection.PLAY_TO_SERVER)
                .decoder(CtSRequestManageScreenPacket::decode)
                .encoder(CtSRequestManageScreenPacket::encode)
                .consumerMainThread(CtSRequestManageScreenPacket::handle)
                .add();

        net.messageBuilder(SavePlayerGeneticsPacket.class, 46, NetworkDirection.PLAY_TO_SERVER)
                .decoder(SavePlayerGeneticsPacket::decode)
                .encoder(SavePlayerGeneticsPacket::encode)
                .consumerMainThread(SavePlayerGeneticsPacket::handle)
                .add();

        net.messageBuilder(OpenSpecificClanScreen.class, 47, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(OpenSpecificClanScreen::decode)
                .encoder(OpenSpecificClanScreen::encode)
                .consumerMainThread(OpenSpecificClanScreen::handle)
                .add();

        net.messageBuilder(OpenCreateMorphPacket.class, 48, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(OpenCreateMorphPacket::new)
                .encoder(OpenCreateMorphPacket::toBytes)
                .consumerMainThread(OpenCreateMorphPacket::handle)
                .add();

        net.messageBuilder(EmoteMorphPacket.class, 49, NetworkDirection.PLAY_TO_SERVER)
                .decoder(EmoteMorphPacket::decode)
                .encoder(EmoteMorphPacket::encode)
                .consumerMainThread(EmoteMorphPacket::handle)
                .add();

        net.messageBuilder(CtSShareMorphToChat.class, 50, NetworkDirection.PLAY_TO_SERVER)
                .decoder(CtSShareMorphToChat::decode)
                .encoder(CtSShareMorphToChat::encode)
                .consumerMainThread(CtSShareMorphToChat::handle)
                .add();

        net.messageBuilder(CtSSaveChatMorphPacket.class, 51, NetworkDirection.PLAY_TO_SERVER)
                .decoder(CtSSaveChatMorphPacket::decode)
                .encoder(CtSSaveChatMorphPacket::encode)
                .consumerMainThread(CtSSaveChatMorphPacket::handle)
                .add();


        net.messageBuilder(StCFinallySaveMorph.class, 52, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(StCFinallySaveMorph::decode)
                .encoder(StCFinallySaveMorph::encode)
                .consumerMainThread(StCFinallySaveMorph::handle)
                .add();

        net.messageBuilder(CtSDismountEaglePacket.class, 53, NetworkDirection.PLAY_TO_SERVER)
                .decoder(CtSDismountEaglePacket::new)
                .encoder(CtSDismountEaglePacket::toBytes)
                .consumerMainThread(CtSDismountEaglePacket::handle)
                .add();

        net.messageBuilder(CallEaglesPacket.class, 54, NetworkDirection.PLAY_TO_SERVER)
                .decoder(CallEaglesPacket::decode)
                .encoder(CallEaglesPacket::encode)
                .consumerMainThread(CallEaglesPacket::handle)
                .add();

        net.messageBuilder(SyncTerritoryToClients.class, 55, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncTerritoryToClients::decode)
                .encoder(SyncTerritoryToClients::encode)
                .consumerMainThread(SyncTerritoryToClients::handle)
                .add();

        net.messageBuilder(CtSClaimTerritory.class, 56, NetworkDirection.PLAY_TO_SERVER)
                .decoder(CtSClaimTerritory::decode)
                .encoder(CtSClaimTerritory::encode)
                .consumerMainThread(CtSClaimTerritory::handle)
                .add();


        net.messageBuilder(CtSUnclaimTerritory.class, 57, NetworkDirection.PLAY_TO_SERVER)
                .decoder(CtSUnclaimTerritory::decode)
                .encoder(CtSUnclaimTerritory::encode)
                .consumerMainThread(CtSUnclaimTerritory::handle)
                .add();

        net.messageBuilder(CtSSendPatrol.class, 58, NetworkDirection.PLAY_TO_SERVER)
                .decoder(CtSSendPatrol::decode)
                .encoder(CtSSendPatrol::encode)
                .consumerMainThread(CtSSendPatrol::handle)
                .add();

        net.messageBuilder(CtSRequestPatrolData.class, 59, NetworkDirection.PLAY_TO_SERVER)
                .decoder(CtSRequestPatrolData::decode)
                .encoder(CtSRequestPatrolData::encode)
                .consumerMainThread(CtSRequestPatrolData::handle)
                .add();

        net.messageBuilder(StCOpenPatrolScreenPacket.class, 60, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(StCOpenPatrolScreenPacket::decode)
                .encoder(StCOpenPatrolScreenPacket::encode)
                .consumerMainThread(StCOpenPatrolScreenPacket::handle)
                .add();

        net.messageBuilder(CompareVersionsPacket.class, 61, NetworkDirection.PLAY_TO_SERVER)
                .decoder(CompareVersionsPacket::decode)
                .encoder(CompareVersionsPacket::encode)
                .consumerMainThread(CompareVersionsPacket::handle)
                .add();

        net.messageBuilder(OpenPlayerCatDataScreenPacket.class, 62, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(OpenPlayerCatDataScreenPacket::new)
                .encoder(OpenPlayerCatDataScreenPacket::toBytes)
                .consumerMainThread(OpenPlayerCatDataScreenPacket::handle)
                .add();

        net.messageBuilder(PlayerKitPacket.class, 63, NetworkDirection.PLAY_TO_SERVER)
                .decoder(PlayerKitPacket::decode)
                .encoder(PlayerKitPacket::encode)
                .consumerMainThread(PlayerKitPacket::handle)
                .add();

        net.messageBuilder(SummonCustomCatPacket.class, 64, NetworkDirection.PLAY_TO_SERVER)
                .decoder(SummonCustomCatPacket::decode)
                .encoder(SummonCustomCatPacket::encode)
                .consumerMainThread(SummonCustomCatPacket::handle)
                .add();

        net.messageBuilder(SetPosePacket.class, 65, NetworkDirection.PLAY_TO_SERVER)
                .decoder(SetPosePacket::decode)
                .encoder(SetPosePacket::encode)
                .consumerMainThread(SetPosePacket::handle)
                .add();

        net.messageBuilder(OpenPoseMenuPacket.class, 66, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(OpenPoseMenuPacket::new)
                .encoder(OpenPoseMenuPacket::toBytes)
                .consumerMainThread(OpenPoseMenuPacket::handle)
                .add();

        net.messageBuilder(EditProfilePacket.class, 67, NetworkDirection.PLAY_TO_SERVER)
                .decoder(EditProfilePacket::decode)
                .encoder(EditProfilePacket::encode)
                .consumerMainThread(EditProfilePacket::handle)
                .add();

        net.messageBuilder(RenameClanPacket.class, 68, NetworkDirection.PLAY_TO_SERVER)
                .decoder(RenameClanPacket::decode)
                .encoder(RenameClanPacket::encode)
                .consumerMainThread(RenameClanPacket::handle)
                .add();

        net.messageBuilder(CtSClimbPacket.class, 69, NetworkDirection.PLAY_TO_SERVER)
                .decoder(CtSClimbPacket::new)
                .encoder(CtSClimbPacket::toBytes)
                .consumerMainThread(CtSClimbPacket::handle)
                .add();

        net.messageBuilder(CtSUnlockClimbPacket.class, 70, NetworkDirection.PLAY_TO_SERVER)
                .decoder(CtSUnlockClimbPacket::decode)
                .encoder(CtSUnlockClimbPacket::encode)
                .consumerMainThread(CtSUnlockClimbPacket::handle)
                .add();

        net.messageBuilder(SyncExhaustionPacket.class, 71, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncExhaustionPacket::new)
                .encoder(SyncExhaustionPacket::toBytes)
                .consumerMainThread(SyncExhaustionPacket::handle)
                .add();

        net.messageBuilder(SyncDiseasesPacket.class, 72, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncDiseasesPacket::new)
                .encoder(SyncDiseasesPacket::toBytes)
                .consumerMainThread(SyncDiseasesPacket::handle)
                .add();

        net.messageBuilder(CtSTakeCatPacket.class, 73, NetworkDirection.PLAY_TO_SERVER)
                .decoder(CtSTakeCatPacket::decode)
                .encoder(CtSTakeCatPacket::encode)
                .consumerMainThread(CtSTakeCatPacket::handle)
                .add();

        net.messageBuilder(CtSNameKitPacket.class, 74, NetworkDirection.PLAY_TO_SERVER)
                .decoder(CtSNameKitPacket::new)
                .encoder(CtSNameKitPacket::toBytes)
                .consumerMainThread(CtSNameKitPacket::handle)
                .add();

        net.messageBuilder(OpenChangelogScreenPacket.class, 75, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(OpenChangelogScreenPacket::new)
                .encoder(OpenChangelogScreenPacket::toBytes)
                .consumerMainThread(OpenChangelogScreenPacket::handle)
                .add();

        net.messageBuilder(RenameNametagPacket.class, 76, NetworkDirection.PLAY_TO_SERVER)
                .decoder(RenameNametagPacket::decode)
                .encoder(RenameNametagPacket::encode)
                .consumerMainThread(RenameNametagPacket::handle)
                .add();

        net.messageBuilder(KickNPCPacket.class, 77, NetworkDirection.PLAY_TO_SERVER)
                .decoder(KickNPCPacket::decode)
                .encoder(KickNPCPacket::encode)
                .consumerMainThread(KickNPCPacket::handle)
                .add();


    }


    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

}
