package com.datdeveloper.datfactions.api.events;

import net.minecraft.commands.CommandSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.Nullable;

public abstract class BaseFactionEvent extends Event {
    /**
     * The player that caused the event to be fired, can be null
     */
    @Nullable
    CommandSource instigator;

    BaseFactionEvent(@Nullable CommandSource instigator) {
        this.instigator = instigator;
    }
}
