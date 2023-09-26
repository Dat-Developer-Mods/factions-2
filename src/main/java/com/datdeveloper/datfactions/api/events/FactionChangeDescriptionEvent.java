package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.Faction;
import net.minecraft.commands.CommandSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Events for when a faction changes its description
 * @see FactionChangeDescriptionEvent.Pre
 * @see FactionChangeDescriptionEvent.Post
 */
public abstract class FactionChangeDescriptionEvent extends FactionEvent {
    /** The new description for the faction */
    protected String newDescription;

    /**
     * @param instigator The CommandSource that instigated the event
     * @param faction The faction the event is about
     * @param newDescription The description the faction is changing to
     */
    protected FactionChangeDescriptionEvent(@Nullable final CommandSource instigator, @NotNull final Faction faction, final String newDescription) {
        super(instigator, faction);
        this.newDescription = newDescription;
    }

    /**
     * Get the original description of the faction
     * @return the previous description
     */
    public abstract String getOldDescription();

    /**
     * Get the description the faction is changing to
     * @return the new Description
     */
    public String getNewDescription() {
        return newDescription;
    }

    /**
     * Fired before the faction description changes
     * <br>
     * The purpose of this event is to allow modifying/checking a faction's submitted description before it is applied. For
     * example, filtering or denying profanity.
     * <br>
     * This event {@linkplain HasResult has a result}.<br>
     * To change the result of this event, use {@link #setResult}. Results are interpreted in the following manner:
     * <ul>
     * <li>Allow - The check will succeed, and the description will be set to the value of newDescription</li>
     * <li>Default - The description will be accepted if it meets the configured maximum length requirements</li>
     * <li>Deny - The check will fail, and the description will not be changed.</li>
     * </ul>
     */
    @HasResult
    public static class Pre extends FactionChangeDescriptionEvent {
        /**
         * @param instigator     The CommandSource that instigated the event
         * @param faction        The faction the event is about
         * @param newDescription The description the faction is changing to
         */
        public Pre(final @Nullable CommandSource instigator, final @NotNull Faction faction, final String newDescription) {
            super(instigator, faction, newDescription);
        }

        /** {@inheritDoc} */
        @Override
        public String getOldDescription() {
            return faction.getDescription();
        }

        /**
         * Set the new description
         * @param newDescription The value to use for the faction's new description
         */
        public void setNewDescription(final String newDescription) {
            this.newDescription = newDescription;
        }
    }

    /**
     * Fired after the faction description changes
     * <br>
     * The intention of this event is to allow observing description changes to update other resources
     */
    public static class Post extends FactionChangeDescriptionEvent {
        /** The old description of the faction */
        final String oldDescription;

        /**
         * @param instigator     The CommandSource that instigated the event
         * @param faction        The faction the event is about
         * @param newDescription The description the faction is changing to
         */
        public Post(final @Nullable CommandSource instigator, final @NotNull Faction faction, final String newDescription, final String oldDescription) {
            super(instigator, faction, newDescription);
            this.oldDescription = oldDescription;
        }

        /** {@inheritDoc} */
        @Override
        public String getOldDescription() {
            return oldDescription;
        }
    }
}
