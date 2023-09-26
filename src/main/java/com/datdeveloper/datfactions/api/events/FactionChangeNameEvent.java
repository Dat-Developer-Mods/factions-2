package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.Faction;
import net.minecraft.commands.CommandSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Events for when a faction changes its name
 * @see FactionChangeNameEvent.Pre
 * @see FactionChangeNameEvent.Post
 */
public abstract class FactionChangeNameEvent extends FactionEvent {
    /** The new name of the faction */
    String newName;

    /**
     * @param instigator The CommandSource that instigated the event
     * @param faction The faction the event is about
     * @param newName The new name of the faction
     */
    protected FactionChangeNameEvent(@Nullable final CommandSource instigator, @NotNull final Faction faction, final String newName) {
        super(instigator, faction);
        this.newName = newName;
    }

    /**
     * Get the original name of the faction
     * @return the previous name
     */
    public abstract String getOldName();

    /**
     * Get the newName
     * @return the new Name
     */
    public String getNewName() {
        return newName;
    }

    /**
     * Set the new name
     * @param newName The value to use for the faction's new name
     */
    public void setNewName(final String newName) {
        this.newName = newName;
    }

    /**
     * Fired before the faction Name changes
     * <br>
     * The purpose of this event is to allow modifying/checking a faction's submitted Name before it is applied. For
     * example, filtering or denying profanity.
     * <br>
     * This event {@linkplain HasResult has a result}.<br>
     * To change the result of this event, use {@link #setResult}. Results are interpreted in the following manner:
     * <ul>
     * <li>Allow - The check will succeed, and the name will be set to the value of newName as long as it's unique</li>
     * <li>Default - The name will be accepted if it meets the configured maximum length requirements and is unique</li>
     * <li>Deny - The check will fail, and the name will not be changed.</li>
     * </ul>
     */
    @HasResult
    public static class Pre extends FactionChangeNameEvent {
        /**
         * @param instigator The CommandSource that instigated the event
         * @param faction    The faction the event is about
         * @param newName    The new name of the faction
         */
        protected Pre(@Nullable final CommandSource instigator, @NotNull final Faction faction, final String newName) {
            super(instigator, faction, newName);
        }

        /** {@inheritDoc} */
        @Override
        public String getOldName() {
            return faction.getName();
        }
    }

    /**
     * Fired after a faction changes its name
     * <br>
     * The intention of this event is to allow observing changes to the name to update other resources
     */
    public static class Post extends FactionChangeNameEvent {
        final String oldName;

        /**
         * @param instigator The CommandSource that instigated the event
         * @param faction    The faction the event is about
         * @param newName    The new name of the faction
         */
        protected Post(@Nullable final CommandSource instigator, @NotNull final Faction faction, final String newName, final String oldName) {
            super(instigator, faction, newName);
            this.oldName = oldName;
        }

        /** {@inheritDoc} */
        @Override
        public String getOldName() {
            return oldName;
        }

        @Override
        public void setNewName(final String newName) {
            throw new UnsupportedOperationException("You cannot change the Name in the post event");
        }
    }
}
