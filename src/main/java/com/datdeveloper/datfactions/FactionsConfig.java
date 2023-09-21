package com.datdeveloper.datfactions;

import com.datdeveloper.datfactions.factiondata.EFactionFlags;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

import java.util.HashMap;
import java.util.Map;

public class FactionsConfig {
    // Faction Management
    private static ConfigValue<Integer> maxFactionNameLength;
    private static ConfigValue<Integer> maxFactionDescriptionLength;
    private static ConfigValue<Integer> maxFactionMotdLength;

    private static ConfigValue<Integer> maxFactionRoleNameLength;
    private static ConfigValue<Integer> maxFactionRoles;

    private static ConfigValue<Integer> globalMaxFactionLandCount;

    private static ConfigValue<Integer> factionOfflineExpiryTime;
    private static ConfigValue<Boolean> removePlayerOnBan;

    // Power
    private static ConfigValue<Integer> playerMaxPower;
    private static ConfigValue<Integer> playerMinPower;
    private static ConfigValue<Integer> factionMaxPower;

    private static ConfigValue<Float> powerLandMultiplier;

    private static ConfigValue<Integer> playerPassivePowerGainInterval;
    private static ConfigValue<Integer> playerPassivePowerGainAmount;
    private static ConfigValue<Integer> playerPassiveMaxPowerGainAmount;
    private static final Map<EPlayerPowerGainMultiplierType, ConfigValue<Float>> playerPassivePowerGainSources = new HashMap<>();

    private static ConfigValue<Integer> baseKillPowerGain;
    private static ConfigValue<Integer> baseKillMaxPowerGain;
    private static final Map<EPlayerPowerGainMultiplierType, ConfigValue<Float>> playerKillPowerGainSources = new HashMap<>();

    private static ConfigValue<Integer> baseDeathPowerLoss;
    private static ConfigValue<Integer> baseDeathMaxPowerLoss;
    private static final Map<EPlayerPowerGainMultiplierType, ConfigValue<Float>> playerDeathPowerLossSources = new HashMap<>();

    private static ConfigValue<Float> bonusPowerFlagPassiveMultiplier;
    private static ConfigValue<Float> bonusPowerFlagKillMultiplier;
    private static ConfigValue<Float> bonusPowerFlagDeathMultiplier;

    // Validation
    private static ConfigValue<EValidationType> validateLandOwnership;
    private static ConfigValue<EValidationType> validateEmptyFactions;

    // Griefing
    private static ConfigValue<Boolean> preventPistonGrief;
    private static ConfigValue<Boolean> preventCropTrampling;

    // Misc
    private static ConfigValue<Boolean> useFactionChat;
    private static ConfigValue<Integer> teleportDelay;
    private static final Map<EFactionFlags, ConfigValue<Boolean>> flagWhitelist = new HashMap<>();

    FactionsConfig(final ForgeConfigSpec.Builder builder) {
        builder.push("Faction Management");
        {
            maxFactionNameLength = builder
                    .comment("The maximum length a faction's name can be")
                    .defineInRange("MaxFactionNameLength", 20, 0, Integer.MAX_VALUE);
            maxFactionDescriptionLength = builder
                    .comment("The maximum length a faction's description can be")
                    .defineInRange("MaxFactionDescriptionLength", 120, 0, Integer.MAX_VALUE);
            maxFactionMotdLength = builder
                    .comment("The maximum length a faction's MOTD can be")
                    .defineInRange("MaxFactionMotdLength", 120, 0, Integer.MAX_VALUE);

            maxFactionRoleNameLength = builder
                    .comment("The maximum length a faction role's name can be")
                    .defineInRange("MaxFactionRoleNameLength", 20, 0, Integer.MAX_VALUE);
            maxFactionRoles = builder
                    .comment("The maximum amount of roles a faction can have")
                    .defineInRange("MaxFactionRoles", 120, 0, Integer.MAX_VALUE);

            globalMaxFactionLandCount = builder
                    .comment("The total maximum amount of chunks a faction can have across all worlds")
                    .defineInRange("globalMaxFactionLandCount", 100, 0, Integer.MAX_VALUE);

            factionOfflineExpiryTime = builder
                    .comment("The amount of time a faction can spend offline before it is deleted in seconds, set to 0 to disable")
                    .defineInRange("FactionOfflineExpiryTime", 0, 0, Integer.MAX_VALUE);
            removePlayerOnBan = builder
                    .comment("Whether to remove a player's faction data when they are banned from the server")
                    .define("RemovePlayerDataOnBan", true);
        } builder.pop();

        builder.push("Power");
        {
            playerMaxPower = builder
                    .comment("The maximum amount of power a player can have")
                    .defineInRange("PlayerMaxPower", 200, 0, Integer.MAX_VALUE);
            playerMinPower = builder
                    .comment("The minimum amount of power a player can have")
                    .define("PlayerMinPower", 0);
            factionMaxPower = builder
                    .comment("The maximum amount of power a faction can have")
                    .defineInRange("FactionMaxPower", 5000, 0, Integer.MAX_VALUE);
            powerLandMultiplier = builder
                    .comment("How much to multiply the faction power by to get the amount of chunk worth the faction is able to hold")
                    .define("PowerLandMultiplier", 1.f);

            builder
                    .comment("Config for power gained passively by the player")
                    .push("Passive Power Gain");
            {
                playerPassivePowerGainInterval = builder
                        .comment("The amount of time in seconds between a player passively gaining max power")
                        .defineInRange("PlayerPassivePowerGainInterval", 1800, 0, Integer.MAX_VALUE);
                playerPassivePowerGainAmount = builder
                        .comment("The base amount of power a player gains passively just by being online (up to their maximum power)")
                        .define("PlayerPassivePowerGainAmount", 7);
                playerPassiveMaxPowerGainAmount = builder
                        .comment("The base amount of max power a player gains passively just by being online")
                        .define("PlayerPassiveMaxPowerGainAmount", 5);
                builder
                        .comment(
                                "Configure the multipliers for passive power gain"
                        )
                        .push("Player Passive Power Gain Multipliers");
                for (final EPlayerPowerGainMultiplierType multiplierType : EPlayerPowerGainMultiplierType.values()) {
                    if (multiplierType.passiveDescription == null) continue;

                    playerPassivePowerGainSources.put(
                            multiplierType,
                            builder
                                    .comment(multiplierType.passiveDescription)
                                    .define(multiplierType.name(), multiplierType.defaultPassiveMultiplier)
                    );
                } builder.pop();
            } builder.pop();

            builder
                    .comment("Config for power gained when the player kills another player")
                    .push("Kill Power Gain");
            {
                baseKillPowerGain = builder
                        .comment("The base amount of power a player gains by killing")
                        .define("BaseKillPowerGain", 5);
                baseKillMaxPowerGain = builder
                        .comment("The base amount of max power a player gains by killing")
                        .define("BaseKillMaxPowerGain", 5);

                builder
                        .comment(
                                "Configure the multipliers for the types of kills that cause the player to gain power",
                                "Set to 0.0 to disable gaining power for that type of kill"
                        )
                        .push("Player Power Gain Multipliers");
                for (final EPlayerPowerGainMultiplierType killType : EPlayerPowerGainMultiplierType.values()) {
                    if (killType.killDescription == null) continue;

                    playerKillPowerGainSources.put(
                            killType,
                            builder
                                    .comment(killType.killDescription)
                                    .define(killType.name(), killType.defaultKillMultiplier)
                    );
                } builder.pop();
            } builder.pop();

            builder
                    .comment("Config for power lost when the player is killed by another player",
                            "Set to 0.0 to disable losing power from that type of death")
                    .push("Death Power Loss");
            {
                baseDeathPowerLoss = builder
                        .comment("The base amount of power a player loses by dying")
                        .define("BaseDeathPowerLoss", -5);
                baseDeathMaxPowerLoss = builder
                        .comment("The base amount of max power a player loses by dying")
                        .define("BaseDeathMaxPowerLoss", 0);
                builder
                        .comment(
                                "Configure the multipliers for the sources of death that cause the player to lose power",
                                "Note these use the true source of the death, IE the entity that shot the arrow, or the entity that hit the player off a cliff"
                        )
                        .push("Player Power Loss Multipliers");
                for (final EPlayerPowerGainMultiplierType deathSource : EPlayerPowerGainMultiplierType.values()) {
                    if (deathSource.deathDescription == null) continue;

                    playerDeathPowerLossSources.put(
                            deathSource,
                            builder
                                    .comment(deathSource.deathDescription)
                                    .define(deathSource.name(), deathSource.defaultDeathMultiplier)
                    );
                } builder.pop();
            } builder.pop();

            builder
                    .comment("Config for the BONUSPOWER faction flag")
                    .push("BONUSPOWER Flag Bonuses");
            {
                bonusPowerFlagPassiveMultiplier = builder
                        .comment("The multiplier for power gain when passively gaining power on land owned by a faction with the BONUSPOWER flag")
                        .define("BonusPowerFlagPassiveMultiplier", 2.f);
                bonusPowerFlagKillMultiplier = builder
                        .comment("The multiplier for power gain when killing on land owned by a faction with the BONUSPOWER flag")
                        .define("BonusPowerFlagKillMultiplier", 2.f);
                bonusPowerFlagDeathMultiplier = builder
                        .comment("The multiplier for power gain when being killed on land owned by a faction with the BONUSPOWER flag")
                        .define("BonusPowerFlagDeathMultiplier", 2.f);
            } builder.pop();
        } builder.pop();

        builder
                .comment(
                        "Validation steps to take during world start",
                        "IGNORE: Don't do anything",
                        "WARN: Print a console message explaining the problem",
                        "REMOVE: Remove the offending problem"
                )
                .push("Validation");
        {
            validateEmptyFactions = builder
                    .comment(
                            "How to handle factions without the PERMANENT flag that have no members"
                    )
                    .defineEnum("ValidateFactionMembers", EValidationType.WARN);

            validateLandOwnership = builder
                    .comment(
                            "How to handle claimed chunks who's owning faction no longer exists",
                            "Note, not removing these may lead to crashes"
                    )
                    .defineEnum("ValidateLandOwnership", EValidationType.REMOVE);
        } builder.pop();

        builder.push("Griefing");
        {
            preventPistonGrief = builder
                    .comment(
                            "Prevent pistons from pushing blocks across faction borders",
                            "Pistons will still be allowed to push blocks between their own borders, and in & out of unowned land"
                    )
                    .worldRestart()
                    .define("PreventPistonGrief", true);
            preventCropTrampling = builder
                    .comment("Prevent players from trampling farmland on owned land")
                    .worldRestart()
                    .define("PreventCropTrampling", true);
        } builder.pop();

        builder.push("Miscellaneous");
        {
            useFactionChat = builder
                    .comment("Enable the faction chat system, allows players to talk with just their faction")
                    .define("UseFactionChat", true);

            teleportDelay = builder
                    .comment("The amount of time a player must stand still in seconds before they teleport to home")
                    .defineInRange("TeleportDelay", 5, 0, Integer.MAX_VALUE);

            builder
                    .comment("Allow/Disallow players setting specific faction flags")
                    .push("Allowed Faction Flags");
            for (final EFactionFlags factionFlag : EFactionFlags.values()) {
                if (factionFlag.admin) continue;

                flagWhitelist.put(
                        factionFlag,
                        builder
                                .comment(factionFlag.description)
                                .define(String.valueOf(factionFlag), true)
                );
            } builder.pop();
        } builder.pop();
    }

    public static int getMaxFactionNameLength() {
        return maxFactionNameLength.get();
    }

    public static int getMaxFactionDescriptionLength() {
        return maxFactionDescriptionLength.get();
    }

    public static int getMaxFactionMotdLength() {
        return maxFactionMotdLength.get();
    }

    public static int getMaxFactionRoleNameLength() {
        return maxFactionRoleNameLength.get();
    }

    public static int getMaxFactionRoles() {
        return maxFactionRoles.get();
    }

    public static int getGlobalMaxFactionLandCount() {
        return globalMaxFactionLandCount.get();
    }

    public static int getFactionOfflineExpiryTime() {
        return factionOfflineExpiryTime.get();
    }

    public static boolean getRemovePlayerOnBan() {
        return removePlayerOnBan.get();
    }

    public static int getPlayerMaxPower() {
        return playerMaxPower.get();
    }
    public static int getPlayerMinPower() {
        return playerMinPower.get();
    }

    public static int getFactionMaxPower() {
        return factionMaxPower.get();
    }

    public static float getPowerLandMultiplier() {
        return powerLandMultiplier.get();
    }

    public static int getPlayerPassivePowerGainInterval() {
        return playerPassivePowerGainInterval.get();
    }

    public static int getPlayerPassivePowerGainAmount() {
        return playerPassivePowerGainAmount.get();
    }

    public static int getPlayerPassiveMaxPowerGainAmount() {
        return playerPassiveMaxPowerGainAmount.get();
    }

    public static float getPassiveMultiplier(final EPlayerPowerGainMultiplierType multiplierType) {
        return playerPassivePowerGainSources.get(multiplierType).get();
    }

    public static int getBaseKillPowerGain() {
        return baseKillPowerGain.get();
    }

    public static int getBaseKillMaxPowerGain() {
        return baseKillMaxPowerGain.get();
    }

    public static float getKillMultiplier(final EPlayerPowerGainMultiplierType multiplierType) {
        return playerKillPowerGainSources.get(multiplierType).get();
    }

    public static int getBaseDeathPowerLoss() {
        return baseDeathPowerLoss.get();
    }

    public static int getBaseDeathMaxPowerLoss() {
        return baseDeathMaxPowerLoss.get();
    }

    public static float getDeathMultiplier(final EPlayerPowerGainMultiplierType multiplierType) {
        return playerDeathPowerLossSources.get(multiplierType).get();
    }

    public static float getBonusPowerFlagPassiveMultiplier() {
        return bonusPowerFlagPassiveMultiplier.get();
    }

    public static float getBonusPowerFlagKillMultiplier() {
        return bonusPowerFlagKillMultiplier.get();
    }

    public static float getBonusPowerFlagDeathMultiplier() {
        return bonusPowerFlagDeathMultiplier.get();
    }

    public static boolean getPreventPistonGrief() {
        return preventPistonGrief.get();
    }

    public static boolean getPreventCropTrampling() {
        return preventCropTrampling.get();
    }

    public static boolean getUseFactionChat() {
        return useFactionChat.get();
    }

    public static int getTeleportDelay() {
        return teleportDelay.get();
    }

    public static boolean getFlagWhitelisted(final EFactionFlags flag) {
        return flagWhitelist.get(flag).get();
    }

    public static EValidationType getValidateLandOwnership() {
        return validateLandOwnership.get();
    }

    public static EValidationType getValidateEmptyFactions() {
        return validateEmptyFactions.get();
    }


    /**
     * The sources of death that cause the player to lose power
     */
    public enum EPlayerPowerGainMultiplierType {
        MOBS(
                null, 0.f,
                "Power gain multiplier when the player kills a mob", 0.f,
                "Power loss multiplier when a mob kills the player", 0.1f
        ),
        NOFACTION(
                "Passive gain multiplier when the player isn't in a faction", 0.f,
                "Power gain multiplier when killing a player who isn't in a faction", 0.f,
                "Power loss multiplier when killed by a player who is not in a faction", 0.5f
        ),
        RECRUIT(
                "Passive gain multiplier when the player is a recruit in a faction", 1.f,
                "Power gain multiplier when killing a recruit from another faction", 1.f,
                "Power loss multiplier when killed by a recruit", 1.f
        ),
        OWNER(
                "Passive gain multiplier when the player is the owner of a faction", 2.f,
                "Power Gain multiplier when killing the owner of a faction", 2.f,
                "Power loss multiplier when killed by the owner of a faction", 1.5f
        ),
        FRIENDLY(
                null, 0.f,
                "Power gain multiplier when killing a player from the same faction (Stacks with other kill sources", 0.f,
                "Power loss multiplier when killed by a player from the same faction (Stacks with other death sources)", 0.f
        ),
        ALLY(
                null, 0.f,
                "Power gain multiplier when killing a player from an allied faction (Stacks with other kill sources", 0.f,
                "Power loss multiplier when killed by a player from an allied faction (Stacks with other death sources)", 0.f
        ),
        TRUCE(
                null, 0.f,
                "Power gain multiplier when killing a player from an faction the player has a truce with (Stacks with other kill sources", 0.f,
                "Power loss multiplier when killed by a player from an faction the player has a truce with (Stacks with other death sources)", 0.f
        ),
        ENEMY(null, 0.f,
                "Power gain multiplier when killing a player from an enemy faction (Stacks with other kill sources)", 2.f,
                "Power loss multiplier when killed by a player from an enemy faction (Stacks with other death sources)", 1.5f
        ),
        SUICIDE(null, 1.f,
                null, 0.f,
                "Power loss multiplier when the player is responsible for killing themselves (Fall damage, hit self with arrow, drowning, etc)", 2.f
        );

        public final String passiveDescription;
        final float defaultPassiveMultiplier;
        final float defaultKillMultiplier;
        public final String killDescription;
        final float defaultDeathMultiplier;
        public final String deathDescription;

        EPlayerPowerGainMultiplierType(final String passiveDescription, final float defaultPassiveMultiplier, final String killDescription, final float defaultKillMultiplier, final String deathDescription, final float defaultDeathMultiplier) {
            this.passiveDescription = passiveDescription;
            this.killDescription = killDescription;
            this.deathDescription = deathDescription;
            this.defaultPassiveMultiplier = defaultPassiveMultiplier;
            this.defaultKillMultiplier = defaultKillMultiplier;
            this.defaultDeathMultiplier = defaultDeathMultiplier;
        }
    }

    /**
     * How to handle validation
     */
    public enum EValidationType {
        /** Ignore the issue */
        IGNORE,
        /** Print a warning message in the console but don't do anything about it */
        WARN,
        /** Remove the offending issue */
        REMOVE
    }
}
