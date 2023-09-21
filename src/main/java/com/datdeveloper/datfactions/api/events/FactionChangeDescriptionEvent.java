package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.Faction;
import net.minecraft.commands.CommandSource;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Events for when the faction description changes
 */
public class FactionChangeDescriptionEvent extends FactionEvent {
    /**
     * The new description for the faction
     */
    String newDescription;

    /**
     * @param instigator The CommandSource that instigated the event
     * @param faction The faction the event is about
     * @param newDescription The description the faction is changing to
     */
    public FactionChangeDescriptionEvent(@Nullable final CommandSource instigator, @NotNull final Faction faction, final String newDescription) {
        super(instigator, faction);
        this.newDescription = newDescription;
    }

    /**
     * Get the description the faction is changing to
     * @return the new Description
     */
    public String getNewDescription() {
        return newDescription;
    }

    /**
     * Set the new description
     * @param newDescription The description the faction will change to
     */
    public void setNewDescription(final String newDescription) {
        this.newDescription = newDescription;
    }

    /**
     * Fired before the faction description changes
     * <br>
     * Can be cancelled, changes to the description will be reflected
     */
    @Cancelable
    @SkipChecks
    public static class Pre extends FactionChangeDescriptionEvent {
        /**
         * @param instigator     The CommandSource that instigated the event
         * @param faction        The faction the event is about
         * @param newDescription The description the faction is changing to
         */
        public Pre(final @Nullable CommandSource instigator, final @NotNull Faction faction, final String newDescription) {
            super(instigator, faction, newDescription);
        }
    }

    /**
     * Fired after the faction description changes
     */
    public static class Post extends FactionChangeDescriptionEvent {
        /**
         * The old description of the faction
         */
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

        /**
         * Get the original value of the faction's description
         * @return the previous description
         */
        public String getOldDescription() {
            return oldDescription;
        }

        @Override
        public void setNewDescription(final String newDescription) {
            throw new UnsupportedOperationException("You cannot change the description in the post event");
        }
    }
}
