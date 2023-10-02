package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.FactionPlayer;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;

/**
 * Parent to events that happen on a faction player
 */
public class FactionPlayerEvent extends Event {
    /**
     * The player the event is for
     */
    @NotNull
    final FactionPlayer player;

    /**
     * @param player The player the event is for
     */
    public FactionPlayerEvent(@NotNull final FactionPlayer player) {
        this.player = player;
    }

    /**
     * Get the player the event is about
     * @return the player the event is about
     */
    public @NotNull FactionPlayer getPlayer() {
        return player;
    }
}
