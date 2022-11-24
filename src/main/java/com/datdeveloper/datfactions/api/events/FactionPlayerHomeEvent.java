package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factionData.FactionPlayer;
import net.minecraft.commands.CommandSource;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called when a player goes to the faction home
 * Cancellable
 */
@Cancelable
public class FactionPlayerHomeEvent extends FactionPlayerEvent {
    public FactionPlayerHomeEvent(@Nullable final CommandSource instigator, @NotNull final FactionPlayer player) {
        super(instigator, player);
    }
}
