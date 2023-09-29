package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.Faction;
import com.datdeveloper.datfactions.factiondata.permissions.FactionRole;
import net.minecraft.commands.CommandSource;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Events for when a faction role changes name
 * @see FactionRoleChangeNameEvent.Pre
 * @see FactionRoleChangeNameEvent.Post
 */
public abstract class FactionRoleChangeNameEvent extends FactionRoleEvent {
    /**
     * The new name of the role
     */
    String newName;

    /**
     * @param instigator The CommandSource that instigated the event
     * @param faction The faction the event is about
     * @param role The role being removed
     * @param newName The new name of the role
     */
    protected FactionRoleChangeNameEvent(@Nullable final CommandSource instigator, @NotNull final Faction faction, @NotNull final FactionRole role, @NotNull final String newName) {
        super(instigator, faction, role);
        this.newName = newName;
    }

    /**
     * Get the new name of the role
     * @return the new name of the role
     */
    public String getNewName() {
        return newName;
    }

    /**
     * Get the name of the role before the change
     * @return The previous name of the role
     */
    public abstract String getOldName();

    /**
     * Fired before the Role name changes
     * <br>
     * The purpose of this event is to allow modifying/checking a role's submitted name before it is applied.
     * For example, to filter or deny profanity
     * <p>After this event, the new name will be checked to ensure it is below the configured maximum length</p>
     * <p>
     *     This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}. <br>
     *     If the event is cancelled, the faction's description will not change.
     * </p>
     */
    public class Pre extends FactionRoleChangeNameEvent {
        /**
         * @param instigator The CommandSource that instigated the event
         * @param faction    The faction the event is about
         * @param role       The role being removed
         * @param newName    The new name of the role
         */
        public Pre(@Nullable final CommandSource instigator, @NotNull final Faction faction, @NotNull final FactionRole role, @NotNull final String newName) {
            super(instigator, faction, role, newName);
        }

        /** {@inheritDoc} */
        @Override
        public String getOldName() {
            return role.getName();
        }

        /**
         * Set the new name of the role
         * @param newName The new name of the role
         */
        public void setNewName(final String newName) {
            this.newName = newName;
        }
    }

    /**
     * Fired after a faction role name changes
     * <br>
     * The intention of this event is to allow observing role name changes to update other resources
     */
    public class Post extends FactionRoleChangeNameEvent {
        /** The old name of the role */
        protected final String oldName;

        /**
         * @param instigator The CommandSource that instigated the event
         * @param faction    The faction the event is about
         * @param role       The role being removed
         * @param newName    The new name of the role
         * @param oldName   The old name of the role
         */
        protected Post(@Nullable final CommandSource instigator,
                       @NotNull final Faction faction,
                       @NotNull final FactionRole role,
                       @NotNull final String newName, final String oldName) {
            super(instigator, faction, role, newName);
            this.oldName = oldName;
        }

        @Override
        public String getOldName() {
            return oldName;
        }
    }
}
