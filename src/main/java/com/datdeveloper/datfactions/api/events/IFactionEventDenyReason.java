package com.datdeveloper.datfactions.api.events;

import net.minecraft.network.chat.Component;

/**
 * An interface providing a method to get and set a reason why an event of which
 * {@linkplain net.minecraftforge.eventbus.api.Event.HasResult has a result}
 * / {@linkplain net.minecraftforge.eventbus.api.Cancelable was cancelled} was denied/cancelled
 */
public interface IFactionEventDenyReason {
    /**
     * Get the reason the event was denied/cancelled
     * @return A component explaining why the event was denied/cancelled
     */
    Component getDenyReason();

    /**
     * Get the denied/cancelled reason, or a default message if one wasn't set
     */
    default Component getDenyReasonOrBackup() {
        final Component reason = getDenyReason();
        if (reason != null) return reason;

        return Component.literal("The action was denied and is unable to finish.");
    }

    /**
     * Set the reason the event was denied/cancelled
     * @param denyReason The reason the event was denied/cancelled
     */
    void setDenyReason(final Component denyReason);
}
