package com.datdeveloper.datfactions.api.events;

import net.minecraft.commands.CommandSource;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.Nullable;

public abstract class BaseFactionEvent extends Event {
    /**
     * The player that caused the event to be fired, can be null
     */
    @Nullable
    final CommandSource instigator;

    BaseFactionEvent(@Nullable final CommandSource instigator) {
        this.instigator = instigator;
    }
}
