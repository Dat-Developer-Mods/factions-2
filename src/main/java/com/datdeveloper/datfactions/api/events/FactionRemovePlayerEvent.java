package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.FactionPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Event for when a player is removed from the faction system. For example if they're banned
 * <br>
 * The intention of this event is to allow observing when a player is removed from the faction's system. For example:
 * to remove them from an external system dependent of DatFactions.
 */
public class FactionRemovePlayerEvent extends FactionPlayerEvent {
    /**
     * @param player     The player the event is for
     */
    public FactionRemovePlayerEvent(@NotNull final FactionPlayer player) {
        super(player);
    }
}
