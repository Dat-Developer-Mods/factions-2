package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.Faction;
import com.datdeveloper.datfactions.factiondata.FactionPlayer;
import net.minecraft.commands.CommandSource;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Events for when a faction changes its owner
 * @see FactionChangeOwnerEvent.Pre
 * @see FactionChangeOwnerEvent.Post
 */
public abstract class FactionChangeOwnerEvent extends FactionEvent {
    /** The new owner of the faction */
    FactionPlayer newOwner;

    /**
     * @param instigator The CommandSource that instigated the event
     * @param faction The faction the event is about
     * @param newOwner The new owner of the faction
     */
    protected FactionChangeOwnerEvent(@Nullable final CommandSource instigator, @NotNull final Faction faction, @Nullable final FactionPlayer newOwner) {
        super(instigator, faction);
        this.newOwner = newOwner;
    }

    /**
     * Get the original owner of the faction
     * @return the previous owner
     */
    public abstract FactionPlayer getOldOwner();

    /**
     * Get the new owner of the faction
     * @return the new owner of the faction
     */
    @NotNull
    public FactionPlayer getNewOwner() {
        return newOwner;
    }

    /**
     * Fired before the faction owner changes
     * <br>
     * The purpose of this event is to allow modifying/checking a faction's change of owner before it is applied, for
     * example, this could be used to prevent a player from being set to the owner of a faction for whitelist reasons
     * <p>
     *     This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.<br>
     *     If the event is cancelled, the faction's owner will not change.
     * </p>
     */
    @Cancelable
    public static class Pre extends FactionChangeOwnerEvent {
        /**
         * @param instigator The CommandSource that instigated the event
         * @param faction    The faction the event is about
         * @param newOwner   The new owner of the faction
         */
        protected Pre(@Nullable final CommandSource instigator, @NotNull final Faction faction, @Nullable final FactionPlayer newOwner) {
            super(instigator, faction, newOwner);
        }

        /** {@inheritDoc} */
        @Override
        public FactionPlayer getOldOwner() {
            return faction.getOwner();
        }

        /**
         * Set the new owner of the faction
         * This must be a member of the faction, otherwise an exception is thrown
         * @param newOwner The new owner of the faction, must be a member of the faction
         * @throws IllegalArgumentException When the newOwner is not a member of the faction
         */
        public void setNewOwner(final @Nullable FactionPlayer newOwner) {
            if (newOwner != null && !newOwner.getFaction().equals(this.faction)) {
                throw new IllegalArgumentException("The new owner must be a member of the faction");
            }

            this.newOwner = newOwner;
        }
    }

    /**
     * Fired after the faction changes owner
     * <br>
     * The intention of this event is to allow observing faction owner changes to update other resources
     */
    public static class Post extends FactionChangeOwnerEvent {
        /** The old owner of the faction */
        final FactionPlayer oldOwner;

        /**
         * @param instigator The CommandSource that instigated the event
         * @param faction    The faction the event is about
         * @param newOwner   The new owner of the faction
         * @param oldOwner   The original owner of the faction
         */
        protected Post(@Nullable final CommandSource instigator, @NotNull final Faction faction, final FactionPlayer newOwner, final FactionPlayer olderOwner, final FactionPlayer oldOwner) {
            super(instigator, faction, newOwner);
            this.oldOwner = oldOwner;
        }

        /** {@inheritDoc} */
        @Override
        public FactionPlayer getOldOwner() {
            return oldOwner;
        }
    }
}
