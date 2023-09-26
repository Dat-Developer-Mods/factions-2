package com.datdeveloper.datfactions.api.events;

import net.minecraft.commands.CommandSource;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.Nullable;

/**
 * Base faction event, implementing stuff common to all faction events
 */
public abstract class BaseFactionEvent extends Event {
    /**
     * The player that caused the event to be fired, can be null
     */
    @Nullable
    final CommandSource instigator;

    /**
     * Whether to skip the default checks
     */
    boolean skipDefaultChecks = false;

    BaseFactionEvent(@Nullable final CommandSource instigator) {
        this.instigator = instigator;
    }

    /**
     * Get the CommandSource that instigated the event
     * @return the CommandSource that instigated the event
     */
    public @Nullable CommandSource getInstigator() {
        return instigator;
    }
}
