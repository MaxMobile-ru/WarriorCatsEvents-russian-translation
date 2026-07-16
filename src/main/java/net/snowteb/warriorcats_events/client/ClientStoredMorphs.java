package net.snowteb.warriorcats_events.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.entity.custom.wcat.WCGenetics;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ClientStoredMorphs {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(MorphsFile.MorphData.class,
                    (InstanceCreator<MorphsFile.MorphData>) type -> new MorphsFile.MorphData())
            .create();
    private static final Path FILE_PATH =
            Minecraft.getInstance().gameDirectory.toPath().resolve("config")
                    .resolve(WarriorCatsEvents.MODID).resolve("morphs.json");

    public static MorphsFile DATA = new MorphsFile();

    public static class MorphsFile {
        public Map<String,MorphData> morphs = new HashMap<>();

        public static class MorphData {
            private final WCGenetics genetics;
            private final WCGenetics chimeraGenetics;
            private final WCGenetics.GeneticalVariants variants;
            private final WCGenetics.GeneticalChimeraVariants chimeraVariants;
            private final boolean onGeneticalSkin;
            private final int presetVariant;

            public MorphData(WCGenetics genetics, WCGenetics chimeraGenetics,
                             WCGenetics.GeneticalVariants variants, WCGenetics.GeneticalChimeraVariants chimeraVariants,
                             boolean onGeneticalSkin, int presetVariant) {
                this.genetics = genetics;
                this.chimeraGenetics = chimeraGenetics;
                this.variants = variants;
                this.chimeraVariants = chimeraVariants;
                this.onGeneticalSkin = onGeneticalSkin;
                this.presetVariant = presetVariant;
            }

            MorphData() {
                this.genetics = new WCGenetics();
                this.chimeraGenetics = new WCGenetics();
                this.variants = new WCGenetics.GeneticalVariants();
                this.chimeraVariants = new WCGenetics.GeneticalChimeraVariants();
                this.onGeneticalSkin = true;
                this.presetVariant = 0;
            }

            public WCGenetics genetics() {
                return this.genetics;
            }

            public WCGenetics chimeraGenetics() {
                return this.chimeraGenetics;
            }

            public WCGenetics.GeneticalVariants variants() {
                return this.variants;
            }

            public WCGenetics.GeneticalChimeraVariants chimeraVariants() {
                return this.chimeraVariants;
            }

            public boolean onGeneticalSkin() {
                return this.onGeneticalSkin;
            }

            public int presetVariant() {
                return this.presetVariant;
            }
        }    }

    public static void load() {
        try {

            if (!Files.exists(FILE_PATH)) {
                Files.createDirectories(FILE_PATH.getParent());
                save();
                return;
            }

            Reader reader = Files.newBufferedReader(FILE_PATH);
            DATA = GSON.fromJson(reader, MorphsFile.class);
            if (DATA == null) {
                DATA = new MorphsFile();
            }

            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {

            Files.createDirectories(FILE_PATH.getParent());

            Writer writer = Files.newBufferedWriter(FILE_PATH);
            GSON.toJson(DATA, writer);
            writer.close();

        } catch (Exception e) {
            WarriorCatsEvents.LOGGER.debug("There was an error while saving the morphs. -" + e.getMessage());
        }
    }

    public static boolean add(String name, MorphsFile.MorphData data, boolean isOverWriting) {
        if (DATA.morphs.containsKey(name) && !isOverWriting) {
            return false;
        }
        DATA.morphs.put(name, data);
        save();
        return true;
    }

    public static boolean remove(String name) {
        if (!DATA.morphs.containsKey(name)) {
            return false;
        }
        DATA.morphs.remove(name);
        save();
        return true;
    }

}
