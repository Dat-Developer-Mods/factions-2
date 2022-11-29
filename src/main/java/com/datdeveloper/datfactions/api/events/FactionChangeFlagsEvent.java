package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factionData.EFactionFlags;
import com.datdeveloper.datfactions.factionData.Faction;
import net.minecraft.commands.CommandSource;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Fired when a faction changes its flags
 * <br>
 * Cancellable, and changes to newFlags will be reflected
 */
@Cancelable
public class FactionChangeFlagsEvent extends FactionEvent {
    /**
     * The new flags the faction will have after the change
     */
    Set<EFactionFlags> newFlags;

    /**
     * @param instigator The CommandSource that instigated the event
     * @param faction The faction the event is about
     * @param newFlags The flags the faction is changing to
     */
    public FactionChangeFlagsEvent(@Nullable final CommandSource instigator, @NotNull final Faction faction, final Set<EFactionFlags> newFlags) {
        super(instigator, faction);
        this.newFlags = newFlags;
    }

    /**
     * Get the flags that the faction is changing to
     * Modifications to this will be reflected
     * @return the new Flags
     */
    public Set<EFactionFlags> getNewFlags() {
        return newFlags;
    }

    /**
     * Set the new flags the faction will change to
     * @param newFlags the new flags
     */
    public void setNewFlags(final Set<EFactionFlags> newFlags) {
        this.newFlags = newFlags;
    }
}
