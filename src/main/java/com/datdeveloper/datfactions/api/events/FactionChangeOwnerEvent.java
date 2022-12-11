package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import net.minecraft.commands.CommandSource;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fired when the faction changes owner
 * <br>
 * Can be cancelled
 */
@Cancelable
public class FactionChangeOwnerEvent extends FactionEvent {
    /**
     * The new owner of the faction
     */
    @NotNull
    final FactionPlayer newOwner;

    /**
     * @param instigator The CommandSource that instigated the event
     * @param faction The faction the event is about
     * @param newOwner The new owner of the faction
     */
    public FactionChangeOwnerEvent(@Nullable final CommandSource instigator, @NotNull final Faction faction, @NotNull final FactionPlayer newOwner) {
        super(instigator, faction);
        this.newOwner = newOwner;
    }

    /**
     * Get the new owner of the faction
     * @return the new owner of the faction
     */
    @NotNull
    public FactionPlayer getNewOwner() {
        return newOwner;
    }
}
