package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.Faction;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;

/**
 * Parent to Events that occur on a faction
 */
public class FactionEvent extends Event {
    /**
     * The faction the player is currently in
     */
    final Faction faction;

    /**
     * @param faction The faction the event is about
     */
    protected FactionEvent(@NotNull final Faction faction) {
        this.faction = faction;
    }

    /**
     * Get the faction the event is about
     * @return the faction the event is about
     */
    public Faction getFaction() {
        return faction;
    }
}
