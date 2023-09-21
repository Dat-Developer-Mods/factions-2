package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.FactionPlayer;
import net.minecraft.commands.CommandSource;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called when a player goes to the faction home
 * <br>
 * Cancellable
 */
@Cancelable
public class FactionPlayerHomeEvent extends FactionPlayerEvent {
    /**
     * @param instigator The CommandSource that instigated the event
     * @param player The player the event is for
     */
    public FactionPlayerHomeEvent(@Nullable final CommandSource instigator, @NotNull final FactionPlayer player) {
        super(instigator, player);
    }
}
