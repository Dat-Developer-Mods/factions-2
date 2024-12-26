package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.data.FactionPlayer;

/**
 * Event fired after a player is newly registered to the Faction System
 */
public class PlayerRegisterEvent extends FactionPlayerEvent {
    /**
     * @param player The player the event is about
     */
    public PlayerRegisterEvent(final FactionPlayer player) {
        super(player);
    }
}
