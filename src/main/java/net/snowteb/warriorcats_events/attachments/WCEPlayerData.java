package net.snowteb.warriorcats_events.attachments;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.snowteb.warriorcats_events.clan.ClanData;
import net.snowteb.warriorcats_events.entity.ModEntities;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCGenetics;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.UUID;

public class WCEPlayerData implements INBTSerializable<CompoundTag> {

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        saveNBT(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        loadNBT(tag);
    }

    public static class PackedData {
        public final String name;
        public final String clanName;
        public final String gender;
        public final String mateName;
        public final Age age;
        public final int kitCooldown;
        public final String bio;


        public PackedData(String name, String clanName, String gender, String mateName, Age age, int kitCooldown, String bio) {
            this.name = name;
            this.clanName = clanName;
            this.gender = gender;
            this.mateName = mateName;
            this.age = age;
            this.kitCooldown = kitCooldown;
            this.bio = bio;
        }
    }

    private String morphName = "None";
    private String clanName = "No clan";
    private String sufix = "";
    private String prefix = "";
    private String characterBio = "";
    private int variantData = 0;
    private int genderData;
    private String genderText = "";
    private boolean firstLoginHandled = false;
    private boolean useSufixes = true;
    private boolean morphNameInChat = true;
    private boolean usingFancyFont = true;
    private UUID mateUUID = ClanData.EMPTY_UUID;
    private BlockPos tempClickedPosData;
    private Component mateName = Component.literal("Undefined");
    private int sleepingCooldown = 0;

    private UUID currentClanUUID = ClanData.EMPTY_UUID;

    private int playerKitsCooldown = 0;


    public enum Age {
        KIT,
        APPRENTICE,
        ADULT
    }

    private Age morphAge = Age.ADULT;


    // GENETICS

    private boolean isOnGeneticalSkin = false;

    private int idlePose = 0;

    private WCGenetics mateGenetics = new WCGenetics();

    private WCGenetics genetics = new WCGenetics();
    private WCGenetics.GeneticalVariants variants = new WCGenetics.GeneticalVariants();
    private WCGenetics chimeraPlayerGenetics = new WCGenetics();
    private WCGenetics.GeneticalChimeraVariants chimeraPlayerVariants = new WCGenetics.GeneticalChimeraVariants();

    public void setMateGenetics(WCGenetics mateGenetics) {
        this.mateGenetics = mateGenetics;
    }
    public WCGenetics getMateGenetics() {
        return mateGenetics;
    }

    public void setPlayerGenetics(WCGenetics genetics) {
        this.genetics = new WCGenetics(genetics);
    }

    public void setPlayerChimeraVariants(WCGenetics.GeneticalChimeraVariants variants) {
        this.chimeraPlayerVariants = new WCGenetics.GeneticalChimeraVariants(variants);
    }
    public WCGenetics.GeneticalChimeraVariants getPlayerChimeraVariants() {
        return chimeraPlayerVariants;
    }

    public void setPlayerChimeraGenetics(WCGenetics gens) {
        this.chimeraPlayerGenetics = new WCGenetics(gens);
    }
    public WCGenetics getPlayerChimeraGenetics() {
        return chimeraPlayerGenetics;
    }

    public void setPlayerGeneticalVariants(WCGenetics.GeneticalVariants genetics) {
        this.variants = new WCGenetics.GeneticalVariants(genetics);
    }

    public WCGenetics getPlayerGenetics() {
        return genetics;
    }

    public WCGenetics.GeneticalVariants getPlayerGeneticalVariants() {
        return variants;
    }

    public boolean isOnGeneticalSkin() {
        return isOnGeneticalSkin;
    }

    public void setOnGeneticalSkin(boolean onGeneticalSkin) {
        isOnGeneticalSkin = onGeneticalSkin;
    }

    // GENETICS

    public UUID getCurrentClanUUID() {
        return currentClanUUID;
    }

    public void setCurrentClanUUID(UUID currentClanUUID) {
        this.currentClanUUID = currentClanUUID;
    }

    public int getSleepingCooldown() {
        return sleepingCooldown;
    }

    public void setSleepingCooldown(int sleepingCooldown) {
        this.sleepingCooldown = sleepingCooldown;
    }

    public BlockPos getTempClickedPosData() {
        return tempClickedPosData;
    }

    public void setTempClickedPosData(BlockPos tempClickedPosData) {
        this.tempClickedPosData = tempClickedPosData;
    }

    public Component getMateName() {
        return mateName;
    }

    public void setMateName(Component mateName) {
        this.mateName = mateName;
    }

    public int getPlayerKitsCooldown() {
        return playerKitsCooldown;
    }

    public void setPlayerKitsCooldown(int playerKitsCooldown) {
        this.playerKitsCooldown = playerKitsCooldown;
    }

    public UUID getMateUUID() {
        return mateUUID;
    }

    public void setMateUUID(UUID mateUUID) {
        this.mateUUID = mateUUID;
    }

    public String getMorphName() {
        return morphName;
    }

    public void setMorphName(String morphName) {
        this.morphName = morphName;
    }

    public void setIdlePose(int idlePose) {
        this.idlePose = idlePose;
    }

    public int getIdlePose() {
        return idlePose;
    }

    public String getClanName() {
        return clanName;
    }

    public String getClanName(ServerLevel level) {
        ClanData data = ClanData.get(level.getServer().overworld());
        ClanData.Clan clan = data.getClan(currentClanUUID);
        if (clan == null) {
            return Component.translatable("generic.no_clan").getString();
        }

        return clan.name;
    }

    public String getClanName(Level level) {
        if (level instanceof ServerLevel sLevel) {
            return getClanName(sLevel);
        }
        return getClanName();
    }

    public void setClanName(String clanName) {
        this.clanName = clanName;
    }

    public String getSufix() {
        return sufix;
    }

    public void setSufix(String sufix) {
        this.sufix = sufix;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public int getVariantData() {
        return variantData;
    }

    public void setVariantData(int variantData) {
        this.variantData = variantData;
    }

    public String getCharacterBio() {
        return characterBio;
    }

    public void setCharacterBio(String characterBio) {
        this.characterBio = characterBio;
    }

    public int getGenderData() {
        return genderData;
    }

    public void setGenderData(int genderData) {
        this.genderData = genderData;
    }

    public String getGenderText() {
        if (genderData == 0) {
            return Component.translatable("generic.wcat.tomcat").getString();
        } else if (genderData == 1) {
            return Component.translatable("generic.wcat.shecat").getString();
        } else {
            if (!genderText.isEmpty()) {
                return genderText;
            }
        }
        return Component.translatable("generic.wcat.non_binary").getString();
    }

    public void setGenderText(String genderText) {
        this.genderText = genderText;
    }

    public Age getMorphAge() {
        return morphAge;
    }

    public void setMorphAge(Age age) {
        this.morphAge = age != null ? age : Age.ADULT;
    }

    public boolean isFirstLoginHandled() {
        return firstLoginHandled;
    }

    public void setFirstLoginHandled(boolean firstLoginHandled) {
        this.firstLoginHandled = firstLoginHandled;
    }

    public boolean isUseSufixes() {
        return useSufixes;
    }

    public void setUseSufixes(boolean useSufixes) {
        this.useSufixes = useSufixes;
    }

    public boolean isMorphNameInChat() {
        return morphNameInChat;
    }
    public void setMorphNameInChat(boolean morphNameInChat) {
        this.morphNameInChat = morphNameInChat;
    }

    public boolean isUsingFancyFont() {
        return usingFancyFont;
    }
    public void setUsingFancyFont(boolean usingFancyFont) {
        this.usingFancyFont = usingFancyFont;
    }

    public void copyFrom(WCEPlayerData source) {
        this.clanName = source.clanName;
        this.variantData = source.variantData;
        this.genderData = source.genderData;
        this.genderText = source.genderText;
        this.morphName = source.morphName;
        this.morphAge = source.morphAge;
        this.firstLoginHandled = source.firstLoginHandled;
        this.prefix = source.prefix;
        this.useSufixes = source.useSufixes;
        this.sufix = source.sufix;
        this.mateUUID = source.mateUUID;
        this.mateName = source.mateName;
        this.tempClickedPosData = source.tempClickedPosData;
        this.currentClanUUID = source.currentClanUUID;

        this.characterBio = source.characterBio;

        this.isOnGeneticalSkin = source.isOnGeneticalSkin;

        this.mateGenetics = source.mateGenetics;

        this.genetics = source.genetics;
        this.variants = source.variants;
        this.chimeraPlayerVariants = source.chimeraPlayerVariants;
        this.chimeraPlayerGenetics = source.chimeraPlayerGenetics;

    }

    public void reset() {
        this.morphName = "<None>";
        this.clanName = "None";
        this.variantData = 0;
        this.genderData = 0;
        this.genderText = "";
        this.morphAge = Age.ADULT;
        this.firstLoginHandled = false;
        this.sufix = "None";
        this.prefix = "None";
        this.useSufixes = true;
        this.tempClickedPosData = null;
        this.sleepingCooldown = 0;
        this.mateGenetics = new WCGenetics();
        this.characterBio = "";

        setDefaultGenetics();
    }

    public void setDefaultGenetics() {

        this.isOnGeneticalSkin = false;

        this.genetics = new WCGenetics();
        this.variants = new WCGenetics.GeneticalVariants();

        this.chimeraPlayerGenetics = new WCGenetics();
        this.chimeraPlayerVariants = new WCGenetics.GeneticalChimeraVariants();
    }

    public void tick() {
        if (sleepingCooldown > 0) {
            sleepingCooldown--;
        }

        if (playerKitsCooldown > 0) {
            playerKitsCooldown--;
        }
    }


    public void saveNBT(CompoundTag nbt) {
        nbt.putString("morphName", this.morphName);
        nbt.putString("clanName", this.clanName);
        nbt.putInt("variantData", this.variantData);
        nbt.putInt("genderData", this.genderData);
        nbt.putString("genderText", this.genderText);
        nbt.putString("morphAge", this.morphAge.name());
        nbt.putString("characterBio", this.characterBio);
        nbt.putBoolean("firstLoginHandled", this.firstLoginHandled);
        nbt.putString("sufix", this.sufix);
        nbt.putString("prefix", this.prefix);
        nbt.putBoolean("useSufixes", this.useSufixes);
        nbt.putBoolean("chatMorphName", this.morphNameInChat);
        nbt.putBoolean("useFancyFont", this.usingFancyFont);
        if (mateUUID != null) {
            nbt.putUUID("mateUUID", mateUUID);
        }
        if (currentClanUUID != null) {
            nbt.putUUID("clanUUID", currentClanUUID);
        }

        nbt.putString("mateName", this.mateName != null ? this.mateName.getString() : "Undefined");

        nbt.putInt("playerKitCooldown", this.playerKitsCooldown);

        CompoundTag geneticsTag = saveGenetics();

        nbt.put("Genetics", geneticsTag);


        if (this.mateGenetics != null) {
            CompoundTag mateTag = new CompoundTag();
            saveMateGenetics(mateTag, this.mateGenetics);
            nbt.put("MateGenetics", mateTag);
        }

    }

    private @NotNull CompoundTag saveGenetics() {
        CompoundTag geneticsTag = new CompoundTag();

        WCGenetics.PackedGeneticData data = new WCGenetics.PackedGeneticData(genetics, variants,
                chimeraPlayerGenetics, chimeraPlayerVariants, isOnGeneticalSkin, variantData);
        WCGenetics.saveModuleNBT(geneticsTag, data);

        geneticsTag.putInt("IdlePose", idlePose);

        return geneticsTag;
    }

    private void loadGenetics(CompoundTag nbt) {
        CompoundTag geneticsTag = nbt.getCompound("Genetics");

        WCGenetics.PackedGeneticData data = WCGenetics.loadModuleNBT(geneticsTag);
        if (data != null) {
            this.setPlayerGenetics(data.genetics);
            this.setPlayerGeneticalVariants(data.variants);
            this.setPlayerChimeraGenetics(data.chimerasGenetics);
            this.setPlayerChimeraVariants(data.chimeraVariants);
            this.setOnGeneticalSkin(data.onGeneticalSkin);
            this.setVariantData(data.morphSkin);
        }

        idlePose = geneticsTag.getInt("IdlePose");
    }

    public void loadNBT(CompoundTag nbt) {
        morphName = nbt.getString("morphName");
        clanName = nbt.getString("clanName");
        variantData = nbt.getInt("variantData");
        genderData = nbt.getInt("genderData");
        if (nbt.contains("genderText")) {
            genderText = nbt.getString("genderText");
        }
        if (nbt.contains("morphAge")) {
            try {
                this.morphAge = Age.valueOf(nbt.getString("morphAge"));
            } catch (IllegalArgumentException e) {
                this.morphAge = Age.ADULT;
            }
        } else {
            this.morphAge = Age.ADULT;
        }
        firstLoginHandled = nbt.getBoolean("firstLoginHandled");
        sufix = nbt.getString("sufix");
        prefix = nbt.getString("prefix");
        useSufixes = nbt.getBoolean("useSufixes");
        if (nbt.contains("chatMorphName")) {
            morphNameInChat = nbt.getBoolean("chatMorphName");
        }
        if (nbt.contains("useFancyFont")) {
            usingFancyFont = nbt.getBoolean("useFancyFont");
        }
        if (nbt.contains("characterBio")) {
            this.characterBio = nbt.getString("characterBio");
        }

        if (nbt.contains("mateUUID")) {
            try {
                mateUUID = nbt.getUUID("mateUUID");
            } catch (Exception e) {
                mateUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
            }
        } else {
            mateUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
        }

        if (nbt.contains("playerKitCooldown")) {
            playerKitsCooldown =  nbt.getInt("playerKitCooldown");
        }

        if (nbt.contains("clanUUID")) {
            try {
                currentClanUUID = nbt.getUUID("clanUUID");
            } catch (Exception e) {
                currentClanUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
            }
        } else {
            currentClanUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
        }


        mateName = Component.literal(nbt.getString("mateName"));

        if (nbt.contains("Genetics")) {
            loadGenetics(nbt);
        }

        if (nbt.contains("MateGenetics")) {
            loadMateGenetics(nbt);
        }

    }

    private void loadMateGenetics(CompoundTag nbt) {
        CompoundTag mateTag = nbt.getCompound("MateGenetics");

        this.mateGenetics = new WCGenetics(
                mateTag.getString("Bobtail"),
                mateTag.getString("ChestFur"),
                mateTag.getString("BellyFur"),
                mateTag.getString("LegsFur"),
                mateTag.getString("HeadFur"),
                mateTag.getString("CheekFur"),
                mateTag.getString("TailFur"),
                mateTag.getString("BackFur"),

                mateTag.getString("Base"),
                mateTag.getString("Orange"),
                mateTag.getString("WhiteRatio"),
                mateTag.getString("Albino"),
                mateTag.getString("Dilute"),
                mateTag.getString("Agouti"),
                mateTag.getString("TabbyStripes"),
                mateTag.getString("EyesAnomaly"),
                mateTag.getString("ChimeraGene"),
                mateTag.getString("Silver")
        );
    }

    private void saveMateGenetics(CompoundTag tag, WCGenetics genetics) {

        tag.putString("ChestFur", genetics.chestFur);
        tag.putString("BellyFur", genetics.bellyFur);
        tag.putString("LegsFur", genetics.legsFur);
        tag.putString("HeadFur", genetics.headFur);
        tag.putString("CheekFur", genetics.cheekFur);
        tag.putString("BackFur", genetics.backFur);
        tag.putString("TailFur", genetics.tailFur);
        tag.putString("Bobtail", genetics.bobtail);

        tag.putString("Base", genetics.base);
        tag.putString("Orange", genetics.orangeBase);
        tag.putString("WhiteRatio", genetics.whiteRatio);
        tag.putString("Albino", genetics.albino);
        tag.putString("Dilute", genetics.dilute);
        tag.putString("Agouti", genetics.agouti);
        tag.putString("TabbyStripes", genetics.tabbyStripes);
        tag.putString("EyesAnomaly", genetics.eyesAnomaly);
        tag.putString("ChimeraGene", genetics.chimeraGene);
        tag.putString("Silver", genetics.silver);

    }
}
