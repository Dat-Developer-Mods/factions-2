package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factionData.FactionPlayer;
import net.minecraft.commands.CommandSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Parent to events that happen on a faction player
 */
public class FactionPlayerEvent extends BaseFactionEvent {
    /**
     * The player the event is for
     */
    @NotNull
    FactionPlayer player;

    public FactionPlayerEvent(@Nullable CommandSource instigator, @NotNull FactionPlayer player) {
        super(instigator);
        this.player = player;
    }

    public @NotNull FactionPlayer getPlayer() {
        return player;
    }
}
