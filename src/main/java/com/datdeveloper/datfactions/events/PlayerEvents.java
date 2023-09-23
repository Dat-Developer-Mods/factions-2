package com.datdeveloper.datfactions.events;

import com.datdeveloper.datfactions.Datfactions;
import com.datdeveloper.datfactions.FactionsConfig;
import com.datdeveloper.datfactions.database.Database;
import com.datdeveloper.datfactions.delayedEvents.PowerDelayedEvent;
import com.datdeveloper.datfactions.factiondata.*;
import com.datdeveloper.datfactions.factiondata.relations.EFactionRelation;
import com.datdeveloper.datfactions.factiondata.relations.FactionRelation;
import com.datdeveloper.datfactions.util.ClaimUtil;
import com.datdeveloper.datfactions.util.PowerUtil;
import com.datdeveloper.datfactions.util.RelationUtil;
import com.datdeveloper.datmoddingapi.delayedEvents.DelayedEventsHandler;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

/**
 * Events pertaining to the players
 */
@Mod.EventBusSubscriber(modid = Datfactions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerEvents {
    /**
     * Register power gain event
     */
    @SubscribeEvent
    public static void playerJoin(final PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof final ServerPlayer player)) return;
        final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(player);
        DelayedEventsHandler.addEvent(new PowerDelayedEvent(fPlayer));
    }

    /**
     * Force save player data
     */
    @SubscribeEvent
    public static void playerLeave(final PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.getEntity() instanceof final ServerPlayer player)) return;
        Database.instance.storePlayer(FPlayerCollection.getInstance().getPlayer(player));
    }

    /**
     * Block damage if the player shares a faction or alliance
     */
    @SubscribeEvent
    public static void playerDamaged(final LivingAttackEvent event) {
        if (!(event.getEntity() instanceof final ServerPlayer target)) return;

        final FactionPlayer targetPlayer = FPlayerCollection.getInstance().getPlayer(target);
        final Faction targetFaction = targetPlayer.getFaction();

        // Friendly Fire
        if (event.getSource().getEntity() instanceof final ServerPlayer source) {
            final FactionPlayer sourcePlayer = FPlayerCollection.getInstance().getPlayer(source);
            final Faction sourceFaction = sourcePlayer.getFaction();
            if (sourceFaction != null && targetFaction != null) {
                if (targetFaction.equals(sourceFaction) && !targetFaction.hasFlag(EFactionFlags.FRIENDLYFIRE)
                ) {
                    event.setCanceled(true);
                    return;
                }

                final FactionRelation fromRelation = sourceFaction.getRelation(targetFaction);
                final FactionRelation toRelation = targetFaction.getRelation(sourceFaction);
                if (fromRelation != null && toRelation != null
                        && fromRelation.getRelation() == EFactionRelation.ALLY
                        && toRelation.getRelation() == EFactionRelation.ALLY
                        && !(targetFaction.hasFlag(EFactionFlags.FRIENDLYFIRE) || sourceFaction.hasFlag(EFactionFlags.FRIENDLYFIRE))
                ) {
                    event.setCanceled(true);
                    return;
                }
            }
        }

        // Land protection
        {
            final FactionLevel level = FLevelCollection.getInstance().getByKey(target.level().dimension());
            final Faction landOwner = level.getChunkOwningFaction(new ChunkPos(target.getOnPos()));
            if (landOwner.hasFlag(EFactionFlags.OPENSHELTER)
                    || (landOwner.equals(targetFaction) && landOwner.hasFlag(EFactionFlags.SHELTERED))
            ) {
                event.setCanceled(true);
            }
        }
    }

    /**
     * Gain Power
     * <br>
     * These functions have been split for readability and simplicities sake. They would be more efficient as one,
     * but it's not worth the headache
     * @see #playerKilled(LivingDeathEvent)
     */
    @SubscribeEvent
    public static void playerKill(final LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof final ServerPlayer source)) return;

        final FactionPlayer sourceFPlayer = FPlayerCollection.getInstance().getPlayer(source);
        final Faction sourceFaction = sourceFPlayer.getFaction();

        final int basePowerChange = FactionsConfig.getBaseKillMaxPowerGain();
        final int baseMaxPowerChange = FactionsConfig.getBaseKillMaxPowerGain();
        final Map<String, Float> multipliers = new HashMap<>();

        // Level multipliers
        {
            final FactionLevel level = FLevelCollection.getInstance().getByKey(source.level().dimension());

            final Faction chunkOwner = level.getChunkOwningFaction(new ChunkPos(source.getOnPos()));
            if (chunkOwner.hasFlag(EFactionFlags.NOPOWER)) return;

            if (chunkOwner.hasFlag(EFactionFlags.BONUSPOWER)) {
                multipliers.put("Bonus", FactionsConfig.getBonusPowerFlagKillMultiplier());
            }
        }

        // Player
        if (event.getEntity() instanceof final ServerPlayer target) {
            final FactionPlayer targetFPlayer = FPlayerCollection.getInstance().getPlayer(target);
            final Faction targetFaction = targetFPlayer.getFaction();

            if (targetFaction == null) {
                multipliers.put("Killed non-faction", FactionsConfig.getKillMultiplier(FactionsConfig.EPlayerPowerGainMultiplierType.NOFACTION));
            } else {
                // Relation
                switch (RelationUtil.getRelation(sourceFaction, targetFaction)) {
                    case ALLY -> {
                        multipliers.put("Killed ally", FactionsConfig.getKillMultiplier(FactionsConfig.EPlayerPowerGainMultiplierType.ALLY));
                    }
                    case TRUCE -> {
                        multipliers.put("Killed truce", FactionsConfig.getKillMultiplier(FactionsConfig.EPlayerPowerGainMultiplierType.TRUCE));
                    }
                    case ENEMY -> {
                        multipliers.put("Killed enemy", FactionsConfig.getKillMultiplier(FactionsConfig.EPlayerPowerGainMultiplierType.ENEMY));
                    }
                    case SELF -> {
                        multipliers.put("Friendly Fire", FactionsConfig.getKillMultiplier(FactionsConfig.EPlayerPowerGainMultiplierType.FRIENDLY));
                    }
                }

                // Role
                final float roleAlpha = (targetFaction.getRoleIndex(targetFPlayer.getRoleId()) / (float) (targetFaction.getRoles().size() - 1));
                multipliers.put("Killed role (" + targetFPlayer.getRole().getName() + ")", ((1 - roleAlpha) * FactionsConfig.getKillMultiplier(FactionsConfig.EPlayerPowerGainMultiplierType.OWNER) + roleAlpha * FactionsConfig.getKillMultiplier(FactionsConfig.EPlayerPowerGainMultiplierType.RECRUIT)));
            }
        }

        // Mob
        else if (event.getEntity() instanceof Mob) {
            multipliers.put("Killed mob", FactionsConfig.getKillMultiplier(FactionsConfig.EPlayerPowerGainMultiplierType.MOBS));
        }

        PowerUtil.handlePowerChange(sourceFPlayer, basePowerChange, baseMaxPowerChange, multipliers);
    }

    /**
     * Lose Power
     * @see #playerKill(LivingDeathEvent)
     */
    @SubscribeEvent
    public static void playerKilled(final LivingDeathEvent event) {
        if (!(event.getEntity() instanceof final ServerPlayer target)) return;

        final FactionPlayer targetFPlayer = FPlayerCollection.getInstance().getPlayer(target);
        final Faction targetFaction = targetFPlayer.getFaction();

        final int basePowerChange = FactionsConfig.getBaseDeathPowerLoss();
        final int baseMaxPowerChange = FactionsConfig.getBaseDeathMaxPowerLoss();
        final Map<String, Float> multipliers = new HashMap<>();

        // Level multipliers
        {
            final FactionLevel level = FLevelCollection.getInstance().getByKey(target.level().dimension());

            final Faction chunkOwner = level.getChunkOwningFaction(new ChunkPos(target.getOnPos()));
            if (chunkOwner.hasFlag(EFactionFlags.NOPOWER)) return;

            if (chunkOwner.hasFlag(EFactionFlags.BONUSPOWER)) {
                multipliers.put("Bonus", FactionsConfig.getBonusPowerFlagDeathMultiplier());
            }
        }

        // Suicide
        if (event.getSource().getEntity() == null || event.getSource().getEntity() == target) {
            multipliers.put("World", FactionsConfig.getDeathMultiplier(FactionsConfig.EPlayerPowerGainMultiplierType.SUICIDE));
        }

        // Player
        else if (event.getSource().getEntity() instanceof final ServerPlayer source) {
            final FactionPlayer sourceFPlayer = FPlayerCollection.getInstance().getPlayer(source);
            final Faction sourceFaction = sourceFPlayer.getFaction();

            if (sourceFaction == null) {
                multipliers.put("Killed by non-faction", FactionsConfig.getDeathMultiplier(FactionsConfig.EPlayerPowerGainMultiplierType.NOFACTION));
            } else {
                // Relation
                switch (RelationUtil.getRelation(targetFaction, sourceFaction)) {
                    case ALLY -> {
                        multipliers.put("Killed by ally", FactionsConfig.getDeathMultiplier(FactionsConfig.EPlayerPowerGainMultiplierType.ALLY));
                    }
                    case TRUCE -> {
                        multipliers.put("Killed by truce", FactionsConfig.getDeathMultiplier(FactionsConfig.EPlayerPowerGainMultiplierType.TRUCE));
                    }
                    case ENEMY -> {
                        multipliers.put("Killed by enemy", FactionsConfig.getDeathMultiplier(FactionsConfig.EPlayerPowerGainMultiplierType.ENEMY));
                    }
                    case SELF -> {
                        multipliers.put("Friendly Fire", FactionsConfig.getDeathMultiplier(FactionsConfig.EPlayerPowerGainMultiplierType.FRIENDLY));
                    }
                }

                // Role
                final float roleAlpha = (sourceFaction.getRoleIndex(sourceFPlayer.getRoleId()) / (float) (sourceFaction.getRoles().size() - 1));
                multipliers.put("Killed by role (" + sourceFPlayer.getRole().getName() + ")", ((1 - roleAlpha) * FactionsConfig.getDeathMultiplier(FactionsConfig.EPlayerPowerGainMultiplierType.OWNER) + roleAlpha * FactionsConfig.getDeathMultiplier(FactionsConfig.EPlayerPowerGainMultiplierType.RECRUIT)));
            }
        }

        // Mob
        else if (event.getSource().getEntity() instanceof Mob) {
            multipliers.put("Killed by mob", FactionsConfig.getDeathMultiplier(FactionsConfig.EPlayerPowerGainMultiplierType.MOBS));
        }

        PowerUtil.handlePowerChange(targetFPlayer, basePowerChange, baseMaxPowerChange, multipliers);
    }

    /**
     * Send enter border message, handle autoclaim
     */
    @SubscribeEvent
    public static void enterChunk(final EntityEvent.EnteringSection event) {
        if (!(event.getEntity() instanceof final ServerPlayer player)) return;
        if (!event.didChunkChange()) return;

        final FactionLevel level = FLevelCollection.getInstance().getByKey(player.level().dimension());

        final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(player);

        // Try autoclaim, if successful then skip notifying the user of chunk change
        final Faction faction = fPlayer.getFaction();
        if (fPlayer.isAutoClaim()) {
            if (ClaimUtil.claimChunks(player, List.of(event.getNewPos().chunk())) > 0) return;
            else {
                if (faction.getTotalLandWorth() + level.getSettings().getLandWorth() > faction.getTotalPower()) {
                    player.sendSystemMessage(Component.literal(DatChatFormatting.TextColour.ERROR + "Your faction has reached the maximum amount of land it can own in this world, disabling autoclaim"));
                    fPlayer.setAutoClaim(false);
                }
            }
        }

        if (fPlayer.getChunkAlertMode() == EFPlayerChunkAlertMode.DISABLED) return;
        final Faction lastChunkOwner = level.getChunkOwningFaction(event.getOldPos().chunk());
        final Faction nextChunkOwner = level.getChunkOwningFaction(event.getNewPos().chunk());

        // Ignore message if the player did not enter territory owned by a different owner to their last chunk
        // Or either owner has the silent flag
        if (Objects.equals(lastChunkOwner, nextChunkOwner) || nextChunkOwner.hasFlag(EFactionFlags.SILENT) || lastChunkOwner.hasFlag(EFactionFlags.SILENT)) return;

        final MutableComponent title = nextChunkOwner.getNameWithDescription(faction)
                                .withStyle(RelationUtil.getRelation(faction, nextChunkOwner).formatting);
        final MutableComponent description = Component.literal(nextChunkOwner.getDescription()).withStyle(ChatFormatting.WHITE);

        switch (fPlayer.getChunkAlertMode()) {
            case TITLE, ACTIONBAR -> {
                player.connection.send(new ClientboundSetTitlesAnimationPacket(10, 40, 10));
                if (fPlayer.getChunkAlertMode() == EFPlayerChunkAlertMode.TITLE) {
                    // This'll cause problems with long descriptions
                    player.connection.send(new ClientboundSetSubtitleTextPacket(description));
                    player.connection.send(new ClientboundSetTitleTextPacket(title));
                } else {
                    // Action bar messages don't support subtitles, annoyingly
                    player.connection.send(new ClientboundSetActionBarTextPacket(Component.literal(DatChatFormatting.TextColour.INFO + "Now entering ")
                            .append(title)
                            .append(DatChatFormatting.TextColour.INFO + " territory")));
                }
            }
            case CHAT -> player.sendSystemMessage(
                   Component.literal(DatChatFormatting.TextColour.INFO + "Now entering ")
                            .append(title)
                            .append(DatChatFormatting.TextColour.INFO + " territory").append("\n")
                            .append(description)
            );
        }
    }

    /**
     * Handle faction chat
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void playerChat(final ServerChatEvent event) {
        if (!FactionsConfig.getUseFactionChat()) return;

        final ServerPlayer player = event.getPlayer();
        final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(player);
        final Faction faction = fPlayer.getFaction();

        if (faction == null || fPlayer.getChatMode() == EFPlayerChatMode.PUBLIC) return;

        final MutableComponent message = Component.empty();
        message.append("<").append(event.getUsername()).append("> ");
        message.append(event.getMessage());

        final List<Faction> targets = new ArrayList<>();
        targets.add(faction);
        if (fPlayer.getChatMode() == EFPlayerChatMode.ALLY) {
            faction.getRelations().values().stream()
                    .filter(relation -> {
                        if (relation.getRelation() != EFactionRelation.ALLY) return false;
                        final FactionRelation otherRelation = relation.getFaction().getRelation(faction);
                        return otherRelation != null && otherRelation.getRelation() == EFactionRelation.ALLY;
                    })
                    .forEach(relation -> targets.add(relation.getFaction()));
        }

        for (final Faction target : targets) {
            final MutableComponent component = Component.empty();
            component.append(
                    Component.empty()
                            .withStyle(RelationUtil.getRelation(target, faction).formatting)
                            .append("[").append(faction.getNameWithDescription(target)).append("]")
            ).append(" ");
            component.append(message);
            target.sendFactionWideMessage(component);
        }

        event.setCanceled(true);
    }
}
