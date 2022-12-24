package com.datdeveloper.datfactions;

import com.datdeveloper.datfactions.factionData.EFactionFlags;
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

    private static ConfigValue<Long> factionOfflineExpiryTime;
    private static ConfigValue<Boolean> removePlayerOnBan;

    // Power
    private static ConfigValue<Integer> playerMaxPower;
    private static ConfigValue<Integer> playerMinPower;
    private static ConfigValue<Integer> factionMaxPower;

    private static ConfigValue<Float> powerLandMultiplier;

    private static ConfigValue<Integer> playerPassivePowerGainInterval;
    private static ConfigValue<Integer> playerPassivePowerGainAmount;
    private static ConfigValue<Integer> playerPassiveMaxPowerGainAmount;
    private static ConfigValue<Float> ownerRolePassivePowerGainMultiplier;
    private static ConfigValue<Float> recruitRolePassivePowerGainMultiplier;

    private static ConfigValue<Integer> baseKillPowerGain;
    private static ConfigValue<Integer> baseKillMaxPowerGain;
    private static ConfigValue<Float> noFactionKillPowerMultiplier;
    private static ConfigValue<Float> enemyKillPowerMultiplier;
    private static ConfigValue<Float> ownerRoleKillPowerMultiplier;
    private static ConfigValue<Float> recruitRoleKillPowerMultiplier;

    private static ConfigValue<Float> bonusPowerFlagMultiplier;

    // Validation
    private static ConfigValue<EValidationType> validateLandOwnership;
    private static ConfigValue<EValidationType> validateEmptyFactions;


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
                    .comment("The amount of time a faction can spend offline before it is deleted in milliseconds, set to 0 to disable")
                    .defineInRange("FactionOfflineExpiryTime", 0, 0, Long.MAX_VALUE);
            removePlayerOnBan = builder
                    .comment("Whether to remove the player's info when they are banned from the server")
                    .define("RemovePlayerInfoOnBan", true);
        } builder.pop();

        builder.push("Power");
        {
            playerMaxPower = builder
                    .comment("The maximum amount of power a player can have")
                    .defineInRange("PlayerMaxPower", 200, 0, Integer.MAX_VALUE);
            playerMinPower = builder
                    .comment("The minimum amount of power a player can have")
                    .define("PlayerMaxPower", 0);
            factionMaxPower = builder
                    .comment("The maximum amount of power a faction can have")
                    .defineInRange("FactionMaxPower", 0, 0, Integer.MAX_VALUE);

            powerLandMultiplier = builder
                    .comment("How much to multiply the faction power by to get the amount of chunk worth the faction is able to hold")
                    .define("PowerLandMultiplier", 1.f);

            playerPassivePowerGainInterval = builder
                    .comment("The amount of time in seconds between a player passively gaining max power")
                    .defineInRange("PlayerPassivePowerGainInterval", 1800, 0, Integer.MAX_VALUE);
            playerPassivePowerGainAmount = builder
                    .comment("The base amount of power a player gains passively just by being online (up to their maximum power)")
                    .define("PlayerPassivePowerGainAmount", 7);
            playerPassiveMaxPowerGainAmount = builder
                    .comment("The base amount of max power a player gains passively just by being online")
                    .define("PlayerPassiveMaxPowerGainAmount", 5);

            ownerRolePassivePowerGainMultiplier = builder
                    .comment(
                            "The multiplier for passive power gain for being the owner of the faction",
                            "The multiplier for other roles linearly interpolated between this value and the RecruitRolePassivePowerGainMultiplier based off their position in the faction hierarchy"
                    )
                    .define("OwnerRolePassivePowerGainMultiplier", 2.f);
            recruitRolePassivePowerGainMultiplier = builder
                    .comment(
                            "The multiplier for passive power gain for being a recruit in the faction",
                            "The multiplier for other roles linearly interpolated between OwnerRolePassivePowerGainMultiplier and this value based off their position in the faction hierarchy"
                    )
                    .define("RecruitRolePassivePowerGainMultiplier", 1.f);

            baseKillPowerGain = builder
                    .comment("The base amount of power a player gains by killing a player")
                    .define("BaseKillPowerGain", 5);
            baseKillMaxPowerGain = builder
                    .comment("The base amount of max power a player gains by killing a player")
                    .define("BaseKillMaxPowerGain", 5);

            noFactionKillPowerMultiplier = builder
                    .comment("The multiplier for the amount of power a player gains by killing a player who isn't in a faction")
                    .define("NoFactionKillPowerMultiplier", 0.f);
            enemyKillPowerMultiplier = builder
                    .comment("The multiplier for the amount of power a player gains by killing a player who is in an enemy faction")
                    .define("EnemyKillPowerMultiplier", 2.f);

            ownerRoleKillPowerMultiplier = builder
                .comment(
                        "The multiplier for power gain for killing the owner of a faction",
                        "The multiplier for other roles linearly interpolated between this value and the value of RecruitRolePassivePowerGainMultiplier off their position in the faction hierarchy"
                )
                .define("OwnerRoleKillPowerMultiplier", 2.f);
            recruitRoleKillPowerMultiplier = builder
                .comment(
                        "The multiplier for power gain for killing a recruit of a faction",
                        "The multiplier for other roles linearly interpolated between OwnerRoleKillPowerMultiplier and this value based off their position in the faction hierarchy"
                )
                .define("RecruitRoleKillPowerMultiplier", 1.f);

            bonusPowerFlagMultiplier = builder.define("BonusPowerFlagMultiplier", 2.f);
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
                    .defineEnum("ValidateFactionMembers", EValidationType.REMOVE);
        } builder.pop();

        builder.push("Miscellaneous");
        {
            useFactionChat = builder
                    .comment("Allow Enable the faction chat system, allows players to talk with just their faction")
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

    public static long getFactionOfflineExpiryTime() {
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

    public static float getOwnerRolePassivePowerGainMultiplier() {
        return ownerRolePassivePowerGainMultiplier.get();
    }

    public static float getRecruitRolePassivePowerGainMultiplier() {
        return recruitRolePassivePowerGainMultiplier.get();
    }

    public static int getBaseKillPowerGain() {
        return baseKillPowerGain.get();
    }

    public static int getBaseKillMaxPowerGain() {
        return baseKillMaxPowerGain.get();
    }

    public static float getNoFactionKillPowerMultiplier() {
        return noFactionKillPowerMultiplier.get();
    }

    public static float getEnemyKillPowerMultiplier() {
        return enemyKillPowerMultiplier.get();
    }

    public static float getOwnerRoleKillPowerMultiplier() {
        return ownerRoleKillPowerMultiplier.get();
    }

    public static float getRecruitRoleKillPowerMultiplier() {
        return recruitRoleKillPowerMultiplier.get();
    }

    public static float getBonusPowerFlagMultiplier() {
        return bonusPowerFlagMultiplier.get();
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
