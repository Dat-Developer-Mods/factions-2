package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factionData.Faction;
import net.minecraft.commands.CommandSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Parent to Events that happen on a faction
 */
public class FactionEvent extends BaseFactionEvent {
    /**
     * The faction the player is currently in
     */
    final Faction faction;

    /**
     * @param instigator The CommandSource that instigated the event
     * @param faction The faction the event is about
     */
    FactionEvent(@Nullable final CommandSource instigator, @NotNull final Faction faction) {
        super(instigator);
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
