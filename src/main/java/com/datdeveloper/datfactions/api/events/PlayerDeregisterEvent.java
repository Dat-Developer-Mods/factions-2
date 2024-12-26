package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.data.FactionPlayer;

/**
 * Event fired after a player has been deregistered from the factions system
 */
public class PlayerDeregisterEvent extends FactionPlayerEvent {
    /**
     * @param player The player that has been deregistered
     */
    public PlayerDeregisterEvent(final FactionPlayer player) {
        super(player);
    }
}
