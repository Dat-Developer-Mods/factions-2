package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.Faction;
import net.minecraft.commands.CommandSource;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fired when the faction name changes
 * <br>
 * Can be cancelled, changes to the name will be reflected
 */
@Cancelable
public class FactionChangeNameEvent extends FactionEvent {
    /**
     * The new name of the faction
     */
    String newName;

    /**
     * @param instigator The CommandSource that instigated the event
     * @param faction The faction the event is about
     * @param newName The new name of the faction
     */
    public FactionChangeNameEvent(@Nullable final CommandSource instigator, @NotNull final Faction faction, final String newName) {
        super(instigator, faction);
        this.newName = newName;
    }

    /**
     * Get the newName
     * @return the newName
     */
    public String getNewName() {
        return newName;
    }

    /**
     * Set the new name
     * @param newName The newName
     */
    public void setNewName(final String newName) {
        this.newName = newName;
    }
}
