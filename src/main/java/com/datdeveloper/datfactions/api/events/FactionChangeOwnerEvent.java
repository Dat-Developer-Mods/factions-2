package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.Faction;
import com.datdeveloper.datfactions.factiondata.FactionPlayer;
import net.minecraft.commands.CommandSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Events for when a faction changes its owner
 *
 */
public abstract class FactionChangeOwnerEvent extends FactionEvent {
    /** The new owner of the faction */
    @NotNull
    FactionPlayer newOwner;

    /**
     * @param instigator The CommandSource that instigated the event
     * @param faction The faction the event is about
     * @param newOwner The new owner of the faction
     */
    protected FactionChangeOwnerEvent(@Nullable final CommandSource instigator, @NotNull final Faction faction, @NotNull final FactionPlayer newOwner) {
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
     * Set the new owner of the faction
     * This must be a member of the faction, otherwise an exception is thrown
     * @param newOwner The new owner of the faction, must be a member of the faction
     */
    public void setNewOwner(final @NotNull FactionPlayer newOwner) throws IllegalAccessException {
        if (!newOwner.getFaction().equals(this.faction)) {
            throw new IllegalAccessException("The new owner must be a member of the faction");
        }

        this.newOwner = newOwner;
    }

    public static class Pre extends FactionChangeOwnerEvent {
        /**
         * @param instigator The CommandSource that instigated the event
         * @param faction    The faction the event is about
         * @param newOwner   The new owner of the faction
         */
        protected Pre(@Nullable CommandSource instigator, @NotNull Faction faction, @NotNull FactionPlayer newOwner) {
            super(instigator, faction, newOwner);
        }

        @Override
        public FactionPlayer getOldOwner() {
            return faction.getOwnerRole();
        }
    }
}
