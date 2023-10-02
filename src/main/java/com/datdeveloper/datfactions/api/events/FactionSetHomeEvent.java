package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.Faction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Events for when a faction sets its home
 * @see FactionSetHomeEvent.Pre
 * @see FactionSetHomeEvent.Post
 */
public abstract class FactionSetHomeEvent extends FactionEvent {
    /** The new home level */
    ResourceKey<Level> newHomeLevel;

    /** The new home position */
    BlockPos newHomePos;

    /**
     * @param faction The faction the event is about
     * @param newHomeLevel The level the new home is in
     * @param newHomePos The position of the new home
     */
    protected FactionSetHomeEvent(@NotNull final Faction faction, final ResourceKey<Level> newHomeLevel, final BlockPos newHomePos) {
        super(faction);
        this.newHomeLevel = newHomeLevel;
        this.newHomePos = newHomePos;
    }

    /**
     * Get the new home level
     * @return the new home level
     */
    public ResourceKey<Level> getNewHomeLevel() {
        return newHomeLevel;
    }

    /**
     * Get the new home position
     * @return the new home position
     */
    public BlockPos getNewHomePos() {
        return newHomePos;
    }

    /**
     * Get the old home level
     *
     * @return the old home level
     */
    public abstract ResourceKey<Level> getOldHomeLevel();

    /**
     * Get the old home position
     * @return the old home position
     */
    public abstract BlockPos getOldHomePos();

    /**
     * Fired before a faction changes its home location
     * <br>
     * The purpose of this event is to allow modifying/checking when a faction's home changes. For example, redirecting
     * a faction home to a different level.
     * <p>
     *     This event {@linkplain HasResult has a result}.<br>
     *     To change the result of this event, use {@link #setResult}. Results are interpreted in the following manner:
     * </p>
     * <ul>
     *     <li>Allow - The check will succeed, and the faction home will be set</li>
     *     <li>Default - The faction home will be set if the home location is on faction land (and that's required)</li>
     *     <li>Deny - The check will fail, and the faction home will not change</li>
     * </ul>
     * <p>
     *     When setting the result to deny, you should provide a reason with {@link #setDenyReason(Component)} to
     *     allow commands to give a reason for not finishing
     * </p>
     */
    @HasResult
    public static class Pre extends FactionSetHomeEvent implements IFactionPreEvent, IFactionEventDenyReason {
        /** The instigator of the action (if there is one) */
        private final ServerPlayer instigator;

        /** A reason for why the event was denied */
        private Component denyReason = null;

        /**
         * @param instigator   The player that instigated the event
         * @param faction      The faction the event is about
         * @param newHomeLevel The level the new home is in
         * @param newHomePos   The position of the new home
         */
        public Pre(@Nullable final ServerPlayer instigator,
                   @NotNull final Faction faction,
                   final ResourceKey<Level> newHomeLevel,
                   final BlockPos newHomePos) {
            super(faction, newHomeLevel, newHomePos);
            this.instigator = instigator;
        }

        /** {@inheritDoc} */
        @Override
        public ResourceKey<Level> getOldHomeLevel() {
            return faction.getHomeLevel();
        }

        /** {@inheritDoc} */
        @Override
        public BlockPos getOldHomePos() {
            return null;
        }

        /**
         * Set the new home level
         * @param newHomeLevel the new home level
         */
        public void setNewHomeLevel(final ResourceKey<Level> newHomeLevel) {
            this.newHomeLevel = newHomeLevel;
        }

        /**
         * Set the new home pos
         * @param newHomePos The new home pos
         */
        public void setNewHomePos(final BlockPos newHomePos) {
            this.newHomePos = newHomePos;
        }

        /** {@inheritDoc} */
        @Override
        public @Nullable ServerPlayer getInstigator() {
            return instigator;
        }

        /** {@inheritDoc} */
        @Override
        public Component getDenyReason() {
            return denyReason;
        }

        /** {@inheritDoc} */
        @Override
        public void setDenyReason(final Component denyReason) {
            this.denyReason = denyReason;
        }
    }

    /**
     * Fired after a faction's home changes
     * <br>
     * The intention of this event is to allow observing the change in home to update other resources
     */
    public static class Post extends FactionSetHomeEvent {
        /** The old home level */
        ResourceKey<Level> oldHomeLevel;

        /** The old home position */
        BlockPos oldHomePos;

        /**
         * @param faction      The faction the event is about
         * @param newHomeLevel The level the new home is in
         * @param newHomePos   The position of the new home
         */
        public Post(@NotNull final Faction faction,
                    final ResourceKey<Level> oldHomeLevel,
                    final BlockPos oldHomePos,
                    final ResourceKey<Level> newHomeLevel,
                    final BlockPos newHomePos) {
            super(faction, newHomeLevel, newHomePos);
            this.oldHomeLevel = oldHomeLevel;
            this.oldHomePos = oldHomePos;
        }

        /** {@inheritDoc} */
        @Override
        public ResourceKey<Level> getOldHomeLevel() {
            return oldHomeLevel;
        }

        /** {@inheritDoc} */
        @Override
        public BlockPos getOldHomePos() {
            return oldHomePos;
        }
    }
}
