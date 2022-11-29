package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factionData.Faction;
import net.minecraft.commands.CommandSource;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fired when the faction name changes
 * <br>
 * Can be cancelled, changes to the description will be reflected
 */
@Cancelable
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
}
