package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.Faction;
import com.datdeveloper.datfactions.factiondata.permissions.FactionRole;
import net.minecraft.commands.CommandSource;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Events for when a faction reorders its roles
 * @see FactionRoleChangeOrderEvent.Pre
 * @see FactionRoleChangeOrderEvent.Post
 */
@Cancelable
public abstract class FactionRoleChangeOrderEvent extends FactionRoleEvent {
    /**
     * The new index of the role
     */
    int newIndex;

    /**
     * @param instigator The CommandSource that instigated the event
     * @param faction The faction the event is about
     * @param role The role changing order
     * @param newIndex The new index of the role
     */
    protected FactionRoleChangeOrderEvent(@Nullable final CommandSource instigator, @NotNull final Faction faction, @NotNull final FactionRole role, final int newIndex) {
        super(instigator, faction, role);
        this.newIndex = newIndex;
    }

    /**
     * Get the current index of the role
     * @return The previous index of the role
     */
    public abstract int getOldIndex();

    public int getNewIndex() {
        return newIndex;
    }

    /**
     * Fired before a faction's roles are reordered
     * <br>
     * The purpose of this event is to allow modifying/checking when roles are reordered. For example, a role could
     * be denied being reordered above a certain level.
     * <p>
          This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}. <br>
          If the event is cancelled, the role order will not change.
      </p>
     */
    @Cancelable
    public class Pre extends FactionRoleChangeOrderEvent {
        /**
         * @param instigator The CommandSource that instigated the event
         * @param faction    The faction the event is about
         * @param role       The role changing order
         * @param newIndex   The new index of the role
         */
        public Pre(@Nullable final CommandSource instigator, @NotNull final Faction faction, @NotNull final FactionRole role, final int newIndex) {
            super(instigator, faction, role, newIndex);
        }

        /**
         * Set the new index of the role
         * @param newIndex The new index of the role
         */
        public void setNewIndex(final int newIndex) {
            if (newIndex < 1 || newIndex >= faction.getRoles().size()) {
                throw new IllegalArgumentException("The new index must be greater than 0 (cannot replace the owner) and"
                        + " less than " + faction.getRoles().size() + " (the number of roles there are)");
            }
            this.newIndex = newIndex;
        }

        /** {@inheritDoc} */
        @Override
        public int getOldIndex() {
            return faction.getRoles().indexOf(role);
        }
    }

    /**
     * Fired after a faction's roles are reordered.
     * <br>
     * The intention of this event is to allow observing faction role order changes to update other resources.
     */
    public class Post extends FactionRoleChangeOrderEvent {
        /** The previous index of the role */
        private final int oldIndex;

        /**
         * @param instigator The CommandSource that instigated the event
         * @param faction    The faction the event is about
         * @param role       The role changing order
         * @param newIndex   The new index of the role
         * @param oldIndex   The previous index of the role
         */
        public Post(@Nullable final CommandSource instigator,
                       @NotNull final Faction faction,
                       @NotNull final FactionRole role,
                       final int newIndex,
                       final int oldIndex) {
            super(instigator, faction, role, newIndex);
            this.oldIndex = oldIndex;
        }

        /** {@inheritDoc} */
        @Override
        public int getOldIndex() {
            return oldIndex;
        }
    }
}
