package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.Faction;
import com.datdeveloper.datfactions.factiondata.FactionPlayer;
import com.datdeveloper.datfactions.factiondata.permissions.FactionRole;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Events for when a new player is registered with DatFactions
 * @see FactionNewPlayerEvent.Pre
 * @see FactionNewPlayerEvent.Post
 */
public abstract class FactionNewPlayerEvent extends FactionPlayerEvent {
    /**
     * @param player     The player the event is for
     */
    protected FactionNewPlayerEvent(@NotNull final FactionPlayer player) {
        super(player);
    }

    /**
     * Get the faction that the player is starting in
     * @return The faction that the player starts in
     */
    public abstract @Nullable Faction getStartingFaction();

    /**
     * Get the role that the player will have in their starting faction
     * @return The role the player starts with
     */
    public abstract @Nullable FactionRole getStartingRole();

    public static class Pre extends FactionNewPlayerEvent implements IFactionPreEvent {
        /** The faction the player will start in */
        @Nullable
        protected Faction startingFaction;

        /** The role the player will start with in their faction */
        @Nullable
        protected FactionRole startingRole;

        /**
         * @param player     The player the event is for
         */
        public Pre(@NotNull final FactionPlayer player) {
            super(player);
            startingFaction = player.getFaction();
            startingRole = player.getRole();
        }

        /** {@inheritDoc} */
        @Override
        public @Nullable Faction getStartingFaction() {
            return startingFaction;
        }

        /** {@inheritDoc} */
        @Override
        public @Nullable FactionRole getStartingRole() {
            return startingRole;
        }

        /**
         * Set the faction and role the player will start with
         * @param startingFaction The faction the player will start in
         * @param startingRole The role the player will start with
         * @throws IllegalArgumentException If the faction and the role aren't both null, or both not null, or if the
         * new role does not belong to the faction
         */
        public void setStartingFaction(final Faction startingFaction, final FactionRole startingRole) {
            if (startingFaction == null && startingRole != null
                    || startingFaction != null && startingRole == null) {
                throw new IllegalArgumentException("Both newRole and newFaction must be null, or not null");
            } else if (startingRole == null || startingFaction.getRole(startingRole.getId()) == null) {
                throw new IllegalArgumentException("newRole must be a role that belongs to newFaction");
            }

            this.startingFaction = startingFaction;
        }

        /**
         * Set the role the player will start with
         * @param startingRole The role the player will start with
         * @throws IllegalArgumentException If the faction is null, or if the new role does not belong to the faction
         */
        public void setStartingRole(final FactionRole startingRole) {
            if (getStartingFaction() == null) {
                throw new IllegalArgumentException("You can't set the newRole when the player isn't joining a faction");
            } else if (startingRole == null || getStartingFaction().getRole(startingRole.getId()) == null) {
                throw new IllegalArgumentException("newRole must be a role that belongs to newFaction");
            }

            this.startingRole = startingRole;
        }

        /** {@inheritDoc} */
        @Override
        public @Nullable ServerPlayer getInstigator() {
            return null;
        }
    }

    public static class Post extends FactionNewPlayerEvent {
        /**
         * @param player     The player the event is for
         */
        public Post(@NotNull final FactionPlayer player) {
            super(player);
        }

        /** {@inheritDoc} */
        @Override
        public @Nullable Faction getStartingFaction() {
            return player.getFaction();
        }

        /** {@inheritDoc} */
        @Override
        public @Nullable FactionRole getStartingRole() {
            return player.getRole();
        }
    }
}
