package aspi.cho.resourcescondenser.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ResourcesCondenserCommonConfigs {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> CONDENSE_SPEED;
    public static final ForgeConfigSpec.ConfigValue<Integer> CONDENSE_PER_OPERATION;
    public static ForgeConfigSpec.ConfigValue<String> CondenseItemDimensionDependencyString;

    static {

        BUILDER.push("Configs for Resources Condenser");

        CONDENSE_SPEED = BUILDER.comment("Time in ticks to generate one strange matter")
                        .define("Condense speed", 40);
        CONDENSE_PER_OPERATION = BUILDER.comment("Amount of strange matter generated per one operation")
                        .defineInRange("Amount", 1 , 1, 64);
        CondenseItemDimensionDependencyString = BUILDER.comment("Condense items dimension dependency." +
                        "\nFormat is {\"dim_id\":{\"item_id\": chance}, \"dim_id\":{\"item_id\": chance} " +
                        "Sum of chances must be 1!")
                .define("Items", "{\"minecraft:the_end\":{\"resources_condenser:strange_matter\": 1}, " +
                        "\"minecraft:overworld\":{\"resources_condenser:strange_matter\": 1}, " +
                        "\"minecraft:the_nether\":{\"resources_condenser:strange_matter\": 1}}");

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
