package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factionData.Faction;
import net.minecraft.commands.CommandSource;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called when a faction is being disbanded
 * Is Cancellable
 */
@Cancelable
public class FactionDisbandEvent extends FactionEvent {
    public FactionDisbandEvent(@Nullable final CommandSource instigator, @NotNull final Faction faction) {
        super(instigator, faction);
    }
}
