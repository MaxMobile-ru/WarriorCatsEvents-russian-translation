package net.snowteb.warriorcats_events.zconfig;

import net.minecraftforge.common.ForgeConfigSpec;

public class WCEServerConfig {

    public static final ForgeConfigSpec SPEC;
    public static final Server SERVER;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        SERVER = new Server(builder);
        SPEC = builder.build();
    }

    public static class Server {

        public final ForgeConfigSpec.IntValue WILDCAT_WANDER_RADIUS;
        public final ForgeConfigSpec.IntValue KITTING_MINUTES;
        public final ForgeConfigSpec.IntValue KIT_GROWTH_MINUTES;
        public final ForgeConfigSpec.DoubleValue SKILL_COST_MULTIPLIER;
        public final ForgeConfigSpec.BooleanValue ENHANCED_ANIMALS;
        public final ForgeConfigSpec.BooleanValue COLORED_NAMES;
        public final ForgeConfigSpec.BooleanValue VISIBLE_MORPH_NAME;
        public final ForgeConfigSpec.BooleanValue LEAP_SERVER;
        public final ForgeConfigSpec.BooleanValue SKILL_TREE_SERVER;
        public final ForgeConfigSpec.BooleanValue VANILLA_MEAT_BONUS;
        public final ForgeConfigSpec.BooleanValue TELEPORT_WHEN_JOIN;
        public final ForgeConfigSpec.BooleanValue CAN_EAGLES_BE_TAMED;
        public final ForgeConfigSpec.BooleanValue THIRST;
        public final ForgeConfigSpec.BooleanValue DISEASES;

        public final ForgeConfigSpec.BooleanValue ENFORCE_TERRITORIES;
        public final ForgeConfigSpec.BooleanValue PROTECT_PLACE_AND_BREAK_BLOCKS;
        public final ForgeConfigSpec.BooleanValue PROTECT_CONTAINERS;

        public final ForgeConfigSpec.DoubleValue SKILL_SPEED_MULTIPLIER;
        public final ForgeConfigSpec.DoubleValue SKILL_HP_MULTIPLIER;
        public final ForgeConfigSpec.DoubleValue SKILL_DMG_MULTIPLIER;
        public final ForgeConfigSpec.DoubleValue SKILL_JUMP_MULTIPLIER;
        public final ForgeConfigSpec.DoubleValue SKILL_ARMOR_MULTIPLIER;

        public final ForgeConfigSpec.IntValue MAX_TERRITORY_TIME;
        public final ForgeConfigSpec.IntValue MAX_TERRITORY_SIZE;



        public Server(ForgeConfigSpec.Builder builder) {
            builder.push("wce_server");

            WILDCAT_WANDER_RADIUS = builder
                    .comment("The radius in which the Wild Cats will wander (min: 8, max: 100)")
                    .defineInRange("wanderRadius", 16, 8, 64);

            KITTING_MINUTES = builder
                    .comment("The time in minutes it takes until she-cats give birth (min: 1, max: 120)")
                    .defineInRange("kittingMinutes", 10, 1, 120);

            KIT_GROWTH_MINUTES = builder
                    .comment("The time in minutes it takes for a kit to grow from kit to warrior (min: 10, max: 960)")
                    .defineInRange("kitGrowthMinutes", 120, 10, 960);

            COLORED_NAMES = builder
                    .comment("Whether Wild Cats' names have a different color depending on their rank")
                    .define("coloredNames", true);

            SKILL_COST_MULTIPLIER = builder
                    .comment("The cost multiplier that will define the skill's cost (min: 0.2, max: 3)")
                    .defineInRange("skillCostValue", 1.0, 0.2, 3.0);

            ENHANCED_ANIMALS = builder
                    .comment("Should wolves and foxes ignore their own behaviour and be aggressive towards Wild Cats and players instead?")
                    .define("aggroAnimals", true);

            VISIBLE_MORPH_NAME = builder
                    .comment("Whether morph names should be visible")
                    .define("visibleMorphName", true);

            LEAP_SERVER = builder
                    .comment("Whether leaping is enabled in this server/world")
                    .define("serverLeap", true);

            SKILL_TREE_SERVER = builder
                    .comment("Whether skills are enabled in this server/world")
                    .define("serverSkills", true);

            VANILLA_MEAT_BONUS = builder
                    .comment("Whether eating raw meats will feed you better")
                    .define("rawFoodBonus", true);

            TELEPORT_WHEN_JOIN = builder
                    .comment("Whether players can choose where to spawn when they join the world")
                    .define("allowTeleport", true);

            CAN_EAGLES_BE_TAMED = builder
                    .comment("Whether eagles can be tamed")
                    .define("canEaglesBeTamed", true);

            THIRST = builder
                    .comment("Whether thirst is enabled")
                    .define("thirst", true);

            DISEASES = builder
                    .comment("Whether the disease system is enabled")
                    .define("diseases", true);

            builder.push("wce_territories");

            MAX_TERRITORY_TIME = builder
                    .comment("The time in minutes it takes for a territory marker to fade.")
                    .defineInRange("fadingTime", 240, 10, Integer.MAX_VALUE);

            MAX_TERRITORY_SIZE = builder
                    .comment("The max number of chunks a clan can possess.")
                    .comment("Increasing this number while having too many clans could be dangerous.")
                    .defineInRange("maxTerritory", 169, 9, Integer.MAX_VALUE);

            ENFORCE_TERRITORIES = builder
                    .comment("Whether territories should be enforced.")
                    .comment("If true, rules configured below will be enforced.")
                    .comment("If false, all rules will be ignored, enforcement wont be set.")
                    .define("enforceTerritories", true);

            PROTECT_PLACE_AND_BREAK_BLOCKS = builder
                    .comment("Whether blocks are protected by clan territory.")
                    .define("placeBreakBlocks", true);

            PROTECT_CONTAINERS = builder
                    .comment("Whether containers are protected by clan territory.")
                    .define("manageContainers", true);


            builder.pop();

            builder.push("wce_skills");

            SKILL_SPEED_MULTIPLIER = builder
                    .comment("The multipliers for each skill.")
                    .comment("This defines how much the skill will increase or decrease its attributes")
                    .defineInRange("speed_Multiplier", 1.0, 0.1, 2.0);
            SKILL_HP_MULTIPLIER = builder
                    .defineInRange("HP_Multiplier", 1.0, 0.1, 2.0);
            SKILL_DMG_MULTIPLIER = builder
                    .defineInRange("damage_Multiplier", 1.0, 0.1, 2.0);
            SKILL_JUMP_MULTIPLIER = builder
                    .defineInRange("jump_Multiplier", 1.0, 0.1, 2.0);
            SKILL_ARMOR_MULTIPLIER = builder
                    .defineInRange("armor_Multiplier", 1.0, 0.1, 2.0);

            builder.pop();
        }
    }
}

