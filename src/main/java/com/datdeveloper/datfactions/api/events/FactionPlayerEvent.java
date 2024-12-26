package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.data.FactionPlayer;
import net.neoforged.bus.api.Event;

/**
 * Parent for events that pertain to a faction player
 */
public abstract class FactionPlayerEvent extends Event {
    /** The player the event is about */
    protected final FactionPlayer player;

    /**
     * @param player The player the event is about
     */
    protected FactionPlayerEvent(final FactionPlayer player) {
        this.player = player;
    }

    public FactionPlayer getPlayer() {
        return player;
    }
}
