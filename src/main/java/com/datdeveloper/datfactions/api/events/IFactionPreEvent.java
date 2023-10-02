package com.datdeveloper.datfactions.api.events;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

/**
 * Interface for methods required for Pre Events
 */
public interface IFactionPreEvent {
    /**
     * Get the player that instigated the event (if there is one)
     * @return the player that instigated the event
     */
    @Nullable
    ServerPlayer getInstigator();
}
