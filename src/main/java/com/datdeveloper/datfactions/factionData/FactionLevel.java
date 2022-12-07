package com.datdeveloper.datfactions.factionData;

import com.datdeveloper.datfactions.database.DatabaseEntity;
import com.datdeveloper.datfactions.util.RelationUtil;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A representation of the level storing the claims in that level and level specific config
 */
public class FactionLevel extends DatabaseEntity {
    /**
     * The ID of the level
     */
    @NotNull
    final ResourceKey<Level> id;

    /**
     * The settings for the level
     */
    FactionLevelSettings settings;

    /**
     * The claims faction's have in the level
     */
    Map<ChunkPos, ChunkClaim> claims;

    // TODO: Add a faction or player blacklist to block a faction in the world

    public FactionLevel(final @NotNull ResourceKey<Level> id) {
        this.id = id;
        settings = null;
        claims = new HashMap<>();
    }

    public FactionLevel(final @NotNull ResourceKey<Level> id, final FactionLevelSettings settings) {
        this.id = id;
        this.settings = settings;
        claims = new HashMap<>();
    }

    /* ========================================= */
    /* Getters
    /* ========================================= */
    public @NotNull ResourceKey<Level> getId() {
        return id;
    }

    public Map<ChunkPos, ChunkClaim> getClaims() {
        return claims;
    }

    public ServerLevel getServerLevel() {
        return ServerLifecycleHooks.getCurrentServer().getLevel(getId());
    }

    public String getName() {
        return getId().location().getPath();
    }

    /* ========================================= */
    /* Claims
    /* ========================================= */
    public int getClaimsCount(@NotNull final UUID factionId) {
        if (factionId.equals(getSettings().defaultOwner)) return Integer.MAX_VALUE;
        return (int) claims.values().stream()
                .filter(claim -> claim.getFactionId().equals(factionId))
                .count();
    }

    public int getClaimsWorth(@NotNull final UUID factionId) {
        if (factionId.equals(getSettings().defaultOwner)) return 0;
        else return getClaimsCount(factionId) * getSettings().landWorth;
    }

    @NotNull
    public UUID getChunkOwner(final ChunkPos pos) {
        final ChunkClaim claim = claims.get(pos);
        return claim != null ? claim.getFactionId() : getSettings().defaultOwner;
    }

    public List<ChunkPos> getFactionChunks(final Faction faction) {
        return claims.keySet().stream()
                .filter(chunk -> claims.get(chunk).factionId.equals(faction.getId()))
                .toList();
    }

    public Faction getChunkOwningFaction(final ChunkPos pos) {
        return FactionCollection.getInstance().getByKey(getChunkOwner(pos));
    }

    public void setChunkOwner(final ChunkPos chunk, final Faction faction) {
        final Faction oldOwner = getChunkOwningFaction(chunk);

        if (faction == null || faction.getId().equals(getSettings().defaultOwner)) {
            claims.remove(chunk);
        } else {
            claims.put(chunk, new ChunkClaim(faction.getId()));
        }
        if (oldOwner != null) oldOwner.validateHome();

        markDirty();
    }


    public void setChunksOwner(final Collection<ChunkPos> chunks, final Faction faction) {
        final Set<Faction> owners = chunks.stream()
                .map(this::getChunkOwningFaction)
                .collect(Collectors.toSet());
        if (faction == null || faction.getId().equals(getSettings().defaultOwner)) {
            claims.keySet().removeAll(chunks);
        } else {
            for (final ChunkPos chunk : chunks) {
                claims.put(chunk, new ChunkClaim(faction.getId()));
            }
        }

        for (final Faction owner : owners) {
            // Null check required as it can be null when disbanding a faction
            if (owner != null) owner.validateHome();
        }

        markDirty();
    }

    public boolean doesChunkConnect(final ChunkPos pos, final Faction faction) {
        return faction.getId().equals(getChunkOwner(new ChunkPos(pos.x + 1, pos.z)))
                || faction.getId().equals(getChunkOwner(new ChunkPos(pos.x, pos.z + 1)))
                || faction.getId().equals(getChunkOwner(new ChunkPos(pos.x - 1, pos.z)))
                || faction.getId().equals(getChunkOwner(new ChunkPos(pos.x, pos.z - 1)));
    }

    /* ========================================= */
    /* Settings
    /* ========================================= */

    /**
     * Get the settings object used by the level
     * If the level doesn't have its own settings, then this will return the default settings
     * Do not modify this object
     * @see #getSettingsToChange()
     * @return The level settings
     */
    public FactionLevelSettings getSettings() {
        return settings != null ? settings : FLevelCollection.getInstance().defaultSettings;
    }

    /**
     * Gets the level specific settings, creating them if they don't yet exist
     * @see #getSettings()
     * @return the level specific changes
     */
    public FactionLevelSettings getSettingsToChange() {
        if (settings == null) settings = new FactionLevelSettings(FLevelCollection.getInstance().defaultSettings);
        markDirty();
        return settings;
    }

    /* ========================================= */
    /* Misc
    /* ========================================= */

    public MutableComponent getNameWithDescription(final Faction from) {
        return Component.literal(getName())
                .withStyle(Style.EMPTY.withHoverEvent(
                        new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                getShortDescription(from)
                        )
                ));
    }

    public MutableComponent getShortDescription(final Faction from) {
        final FactionLevelSettings settings = getSettings();
        final Faction defaultOwner = FactionCollection.getInstance().getByKey(settings.getDefaultOwner());
        return Component.literal(defaultOwner.getName())
                        .withStyle(RelationUtil.getRelation(from, defaultOwner).formatting).append("\n")
                        .append(DatChatFormatting.TextColour.INFO + "Land Worth: ")
                        .append(ChatFormatting.WHITE.toString() + settings.getLandWorth()).append("\n")
                        .append(DatChatFormatting.TextColour.INFO + "Max Land: ")
                        .append(ChatFormatting.WHITE.toString() + (settings.getMaxLand() == Integer.MAX_VALUE ? "∞" : settings.getMaxLand())).append("\n")
                        .append(DatChatFormatting.TextColour.INFO + "Passive Power Gain: ")
                        .append(ChatFormatting.WHITE + String.valueOf(settings.getPassivePowerGainMultiplier()) + "x");
    }
    
    /* ========================================= */
    /* Database Stuff
    /* ========================================= */

    @Override
    public void markClean() {
        super.markClean();
        if (settings != null) settings.markClean();
    }

    @Override
    public boolean isDirty() {
        return super.isDirty() || (settings != null && settings.isDirty());
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return (obj instanceof FactionLevel fLevel) && this.getId().equals(fLevel.getId());
    }
}
