package com.akrozora.customtooltips.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CustomTooltipsConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    static {
        BUILDER.push("CONFIG for Testing Purposes");
        BUILDER.comment("testing purposes");

        SPEC = BUILDER.build();
    }
}
