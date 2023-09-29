package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.FactionPlayer;
import net.minecraft.commands.CommandSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Event for when a player is removed from the faction system. For example if they're banned
 * <br>
 * The intention of this event is to allow observing when a player is removed from the faction's system. For example:
 * to remove them from an external system dependent of DatFactions.
 */
public class FactionRemovePlayerEvent extends FactionPlayerEvent {
    /**
     * @param instigator The CommandSource that instigated the event
     * @param player     The player the event is for
     */
    public FactionRemovePlayerEvent(@Nullable final CommandSource instigator, @NotNull final FactionPlayer player) {
        super(instigator, player);
    }
}
