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
    final FactionPlayer player;

    public FactionPlayerEvent(@Nullable final CommandSource instigator, @NotNull final FactionPlayer player) {
        super(instigator);
        this.player = player;
    }

    public @NotNull FactionPlayer getPlayer() {
        return player;
    }
}
