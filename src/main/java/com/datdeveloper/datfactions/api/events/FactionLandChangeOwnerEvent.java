package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.Faction;
import com.datdeveloper.datfactions.factiondata.FactionLevel;
import net.minecraft.commands.CommandSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Events for when faction land changes owner
 * @see FactionLandChangeOwnerEvent.Pre
 * @see FactionLandChangeOwnerEvent.Post
 */
public abstract class FactionLandChangeOwnerEvent extends BaseFactionEvent {
    /** The chunks being claimed */
    @NotNull
    Set<ChunkPos> chunks;

    /** The level containing the chunks being claimed */
    @NotNull
    FactionLevel level;

    /**
     * The new owner of the chunks
     * <br>
     * If this is null then it can be assumed that the land is being unclaimed
     */
    @Nullable
    Faction newOwner;

    /** The reason the chunks changed ownership */
    final EChangeOwnerReason reason;

    /**
     * @param instigator The CommandSource that instigated the event
     * @param chunks The chunks that are changing owner
     * @param level The level containing the chunks
     * @param newOwner The new owner of the chunks
     * @param reason The reason the land is changing ownership
     */
    protected FactionLandChangeOwnerEvent(@Nullable final CommandSource instigator, @NotNull final Set<ChunkPos> chunks, final @NotNull FactionLevel level, @Nullable final Faction newOwner, final EChangeOwnerReason reason) {
        super(instigator);
        this.chunks = chunks;
        this.level = level;
        this.newOwner = newOwner;
        this.reason = reason;
    }

    /**
     * Get the chunks that are changing owner
     * @return The chunks that are changing owner
     */
    public abstract @NotNull Set<ChunkPos> getChunks();

    /**
     * Get the level containing the chunks are changing owner
     * @return The level containing the chunks that are changing owner
     */
    public @NotNull FactionLevel getLevel() {
        return level;
    }

    /**
     * Get the new owner of the chunks
     * @return The new owner of the chunks
     */
    public @Nullable Faction getNewOwner() {
        return newOwner;
    }

    /**
     * Get the reason the chunks change owner
     * @return The reason for the chunks changing owner
     */
    public EChangeOwnerReason getReason() {
        return reason;
    }

    /**
     * An enum representing the reasons why the land is changing ownership
     */
    public enum EChangeOwnerReason {
        /** The chunks were claimed by a faction */
        CLAIM,
        /** The chunks were unclaimed by a faction */
        UNCLAIM,
        /** The faction owning the chunks is being disbanded */
        DISBAND,
        /** The faction owning the chunks are giving them away */
        GIFT,
        /** The faction owning the chunks was merged into another faction */
        MERGE,
        /** An admin is making manual changes to ownership */
        ADMIN,
        /** A catchall special reason for operations by other mods */
        SPECIAL,
        /** Same as special, but immutable so events cannot modify the chunks or level */
        SPECIALIMMUTABLE
    }

    /**
     * Fired before land changes owner
     * <br>
     * The purpose of this event is to allow modifying/checking when the ownership of land is transferred.
     * For example, checking a faction is high enough level to claim the land, or redirecting the faction that gets
     * the land.
     * <p>
     *     When the reason is claim, unclaim, gift, or special, this event {@linkplain HasResult has a result}.<br>
     *     To change the result of this event, use {@link #setResult}. Results are interpreted in the following manner:
     * </p>
     * <ul>
     *     <li>Allow - The check will succeed, and the land ownership will transfer</li>
     *     <li>Default - The land will be perform the default checks as to whether the new ownership can occur</li>
     *     <li>Deny - The check will fail, and the land will not change ownership</li>
     * </ul>
     */
    @HasResult
    public class Pre extends FactionLandChangeOwnerEvent {
        /**
         * @param instigator The CommandSource that instigated the event
         * @param chunks The chunks that are changing owner
         * @param level The level containing the chunks
         * @param newOwner The new owner of the chunks
         * @param reason The reason the land is changing ownership
         */
        public Pre(@Nullable final CommandSource instigator, @NotNull final Collection<ChunkPos> chunks, @NotNull final FactionLevel level, @Nullable final Faction newOwner, final EChangeOwnerReason reason) {
            super(instigator,
                    new HashSet<>(chunks),
                    level,
                    newOwner,
                    reason);
        }

        /**
         * {@inheritDoc}
         * <br>
         * Note, if the reason is disband, merge, or specialImmutable, you will not be able to modify the chunks
         * @return The chunks that are changing owner
         */
        @Override
        public @NotNull Set<ChunkPos> getChunks() {
            if (List.of(EChangeOwnerReason.DISBAND, EChangeOwnerReason.MERGE, EChangeOwnerReason.SPECIALIMMUTABLE).contains(getReason())) {
                return Collections.unmodifiableSet(chunks);
            }
            return chunks;
        }

        /**
         * Set the chunks that are changing owner
         * <br>
         * Note, this is disabled if the reason is disband, merge, or specialImmutable
         * @param chunks The chunks that are changing owner
         * @throws UnsupportedOperationException When the reason is disband, merge, or specialImmutable
         */
        public void setChunks(final @NotNull Set<ChunkPos> chunks) {
            if (List.of(EChangeOwnerReason.DISBAND, EChangeOwnerReason.MERGE, EChangeOwnerReason.SPECIALIMMUTABLE).contains(getReason())) {
                throw new UnsupportedOperationException("Cannot set chunks when reason is set to " + getReason().name());
            }
            this.chunks = chunks;
        }

        /**
         * Set the level containing the chunks that are being claimed
         * <br>
         * Note, this is disabled if the reason is disband, merge, or specialImmutable
         * @param level The level containing the chunks that are changing owner
         * @throws UnsupportedOperationException When the reason is disband, merge, or specialImmutable
         */
        public void setLevel(final @NotNull FactionLevel level) {
            if (List.of(EChangeOwnerReason.DISBAND, EChangeOwnerReason.MERGE, EChangeOwnerReason.SPECIALIMMUTABLE).contains(getReason())) {
                throw new UnsupportedOperationException("Cannot set the level when reason is set to " + getReason().name());
            }
            this.level = level;
        }

        /**
         * Set the new owner of the chunks
         * @param newOwner The new owner of the chunks
         */
        public void setNewOwner(@Nullable final Faction newOwner) {
            this.newOwner = newOwner;
        }

        @Override
        public boolean hasResult() {
            // Only have a result when the reason is one of these
            return List.of(EChangeOwnerReason.CLAIM, EChangeOwnerReason.UNCLAIM, EChangeOwnerReason.GIFT, EChangeOwnerReason.SPECIAL)
                    .contains(getReason());
        }
    }

    /**
     * Fired after land has changed ownership
     * <br>
     * The intention of this event is to allow observing land ownership changes to update other resources
     */
    public class Post extends FactionLandChangeOwnerEvent {
        /**
         * The previous owners of the chunks
         * <br>
         * A map of the Faction ID to a set of blockpos
         */
        private final Map<UUID, Set<BlockPos>> previousOwners;

        /**
         * @param instigator        The CommandSource that instigated the event
         * @param chunks            The chunks that are changing owner
         * @param level             The level containing the chunks
         * @param newOwner          The new owner of the chunks
         * @param previousOwners    The previous owners of the chunks
         * @param reason            The reason the land is changing ownership
         */
        public Post(@Nullable final CommandSource instigator,
                    @NotNull final Set<ChunkPos> chunks,
                    @NotNull final FactionLevel level,
                    @Nullable final Faction newOwner,
                    final Map<UUID, Set<BlockPos>> previousOwners,
                    final EChangeOwnerReason reason) {
            super(instigator,
                    Collections.unmodifiableSet(chunks),
                    level,
                    newOwner,
                    reason);
            this.previousOwners = previousOwners.entrySet().stream()
                    .map(entry -> Map.entry(entry.getKey(), Collections.unmodifiableSet(entry.getValue())))
                    .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NotNull Set<ChunkPos> getChunks() {
            return chunks;
        }

        /**
         * Get the previous owners of the chunks
         * @return The previous owners of the chunks
         */
        public Map<UUID, Set<BlockPos>> getPreviousOwners() {
            return previousOwners;
        }
    }
}
