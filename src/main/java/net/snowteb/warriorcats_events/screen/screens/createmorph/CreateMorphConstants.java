package net.snowteb.warriorcats_events.screen.screens.createmorph;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.snowteb.warriorcats_events.entity.client.WCModel;

import java.util.ArrayList;
import java.util.List;

public class CreateMorphConstants {

    public record Variant(String titleText, int id, String underText) {
        
        public Component component() {
            return Component.literal(titleText());
        }

        public Component underComponent() {
            return Component.literal(underText());
        }
    }
    
    public static List<Variant> getVariants() {

        List<Variant> variants = new ArrayList<Variant>();

        int i = 0;

        variants.add(new Variant("Calico", i++, ""));
        variants.add(new Variant("Siamese", i++, ""));
        variants.add(new Variant("Gray", i++, ""));
        variants.add(new Variant("Abyssinian", i++, ""));
        variants.add(new Variant("Black", i++, ""));
        variants.add(new Variant("Maine Coon", i++,""));
        variants.add(new Variant("Russian Blue", i++, ""));
        variants.add(new Variant("Dark Brown Tabby", i++, ""));
        variants.add(new Variant("White", i++, ""));
        variants.add(new Variant("Calico 2", i++, ""));
        variants.add(new Variant("Munchkin", i++, ""));
        variants.add(new Variant("Light Gray Tabby", i++, ""));
        variants.add(new Variant("Chestnutpatch", i++, "By Bookwom"));
        variants.add(new Variant("Ratstar", i++, "By Telefonjoker"));
        variants.add(new Variant("Twitchstream", i++, "By Cat"));
        variants.add(new Variant("Blazepit", i++, "By Cat"));
        variants.add(new Variant("Bengalpelt", i++, "By Klyonstar"));
        variants.add(new Variant("Sparrowstar", i++, "By Whale_shark"));
        variants.add(new Variant("Foxeater", i++, "By Sejr"));
        variants.add(new Variant("Willowsong", i++, "By Sejr"));
        variants.add(new Variant("White 2", i++, ""));
        variants.add(new Variant("Dalmatian", i++, ""));
        variants.add(new Variant("Gray Tabby", i++, ""));
        variants.add(new Variant("Brown", i++, ""));
        variants.add(new Variant("Pale Ginger", i++, ""));
        variants.add(new Variant("Black 2", i++, ""));
        variants.add(new Variant("Bengal", i++, ""));
        variants.add(new Variant("Snowshoe", i++, ""));
        variants.add(new Variant("Toyger", i++, ""));
        variants.add(new Variant("Turkish Van", i++, ""));
        variants.add(new Variant("Albino", i++, "By CoffeeCat"));
        variants.add(new Variant("Bengal", i++, "By CoffeeCat"));
        variants.add(new Variant("Brindle Tortie", i++, "By Mswolfy81"));
        variants.add(new Variant("Cream Calico 1", i++, "By Lightley"));
        variants.add(new Variant("Cream Calico 2", i++, "By Lightley"));
        variants.add(new Variant("Cream Calico 3", i++, "By Lightley"));
        variants.add(new Variant("Caramel", i++, "By CoffeeCat"));
        variants.add(new Variant("Frostdawn", i++, "By whitenoisewife"));
        variants.add(new Variant("Gray-white Tabby", i++, "By Slay"));
        variants.add(new Variant("Hailflake", i++, "By pvppet, Mswolfy81"));
        variants.add(new Variant("Karpati", i++, "By whitenoisewife"));
        variants.add(new Variant("Leafstar", i++, "By whitenoisewife"));
        variants.add(new Variant("Longtail", i++, "By whitenoisewife"));
        variants.add(new Variant("Mothpaw", i++, "By CoffeeCat"));
        variants.add(new Variant("Redtail", i++, "By whitenoisewife"));
        variants.add(new Variant("Salem", i++, "By CoffeeCat, Mswolfy81"));
        variants.add(new Variant("Short hair", i++, "By CoffeeCat"));
        variants.add(new Variant("Stoneflare", i++, "By Feathered Melodica"));
        variants.add(new Variant("Tortie point", i++, "By whitenoisewife"));
        variants.add(new Variant("Turtleheart", i++, "By RainbowServal, Mswolfy81"));
        variants.add(new Variant("Violetdew", i++, "By bem te vi, Mswolfy81"));
        variants.add(new Variant("Patch", i++, "By Feathered Melodica"));
        variants.add(new Variant("Parlee", i++, "By PsychicStudios, CoffeeCat"));

        for (ResourceLocation loc : WCModel.PACK_TEXTURES) {
            String name = loc.getPath();
            String[] parts = name.split("/");
            name = parts[parts.length - 1].replace(".png", "");
            if (!name.isEmpty()) {
                name = name.replace(name.charAt(0), Character.toUpperCase(name.charAt(0)));
            }
            variants.add(new Variant(name, i++, Component.translatable("wce.morph_loaded_from_pack").getString()));
        }


        return variants;
    }

    public static final String setBobtail = "b-b";
    public static final String setFulltail = "B-b";

    public static final String setShortFur = "s-s";
    public static final String setLongFur = "L-s";


    public static final String setBlack = "B-b";
    public static final String setChocolate = "b-b1";
    public static final String setCinnamon = "b1-b1";


    public static final String setOrange = "O-O";
    public static final String setTortie = "O-o";
    public static final String setNotOrange = "o-o";

    public static final String setFullWhite = "Wd-w";
    public static final String setHighWhite = "S-S";
    public static final String setLowWhite = "S-w";
    public static final String setNotWhite = "w-w";

    public static final String setNotAlbino = "C-cs";

    public static final String setTrueAlbino = "c-c";
    public static final String setSepia = "cb-c";
    public static final String setMink = "cs-cb";
    public static final String setSiamese = "cs-c";

    public static final String setDilute = "D-d";
    public static final String setNonDilute = "d-d";
    
    public static final String setAgoutiTabby = "A-a";
    public static final String setNonAgoutiTabby = "a-a";

    public static final String setTabbyStripesMackerel = "Mc-mc";
    public static final String setTabbyStripesClassic = "mc-mc";

    public static final String setSilver = "I-i";
    public static final String setNonSilver = "i-i";

    public static final String setEyeBlue = "blue";
    public static final String setEyeYellow = "yellow";
    public static final String setEyeGreen = "green";
    public static final String setEyeRed = "red";
    public static final String setEyeBlind = "blind";

    public static final String setHeteroChromia = "h-h";
    public static final String setNonHeteroChromia = "H-h";

    public static final String setChimera = "c-c";
    public static final String setNotChimera = "C-C";
}
