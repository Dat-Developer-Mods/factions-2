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
    Faction playerFaction;
    FactionEvent(@Nullable CommandSource instigator, @NotNull Faction faction) {
        super(instigator);
        this.playerFaction = faction;
    }

    public Faction getPlayerFaction() {
        return playerFaction;
    }
}
