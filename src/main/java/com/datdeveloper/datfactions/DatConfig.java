package com.datdeveloper.datfactions;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

/**
 * Config for DatFactions
 */
public class DatConfig {


    /* ======================================== */
    /* Power                                    */
    /* ======================================== */

    /** The maximum amount of power a player can hold */
    private static final ModConfigSpec.IntValue MAX_PLAYER_POWER;
    /** The minimum amount of power a player can have */
    private static final ConfigValue<Integer> MIN_PLAYER_POWER;

    /** Mod config specification for NeoForge */
    public static final ModConfigSpec SPEC;

    static {
        final ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("PowerLimits");
        {
            MAX_PLAYER_POWER = builder
                    .comment("The maximum amount of power a player can have.")
                    .comment("When a player gains power, their max power will not exceed this value.")
                    .comment("This value can be overridden by setting the player's power with admin command.")
                    .defineInRange("PlayerMaxPower", 200, 0, Integer.MAX_VALUE);

            MIN_PLAYER_POWER = builder
                    .comment("The minimum amount of power a player can have")
                    .comment("When the player loses power for any reason, their power will not drop below this value.")
                    .define("PlayerMinPower", 0);
        }
        builder.pop();

        SPEC = builder.build();
    }

    /* ======================================== */
    /* Getters                                  */
    /* ======================================== */

    public static int getMaxPlayerPower() {
        return MAX_PLAYER_POWER.get();
    }

    public static int getMinPlayerPower() {
        return MIN_PLAYER_POWER.get();
    }
}
