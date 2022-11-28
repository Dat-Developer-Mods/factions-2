package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factionData.Faction;
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
    String newName;
    public FactionChangeNameEvent(@Nullable final CommandSource instigator, @NotNull final Faction faction, final String newName) {
        super(instigator, faction);
        this.newName = newName;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(final String newName) {
        this.newName = newName;
    }
}
