package com.datdeveloper.datfactions.api.events;

import net.minecraft.commands.CommandSource;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.Nullable;

/**
 * Called when a faction is created
 * <br>
 * Can be cancelled, changes to the name will be reflected
 */
@Cancelable
public class FactionCreateEvent extends BaseFactionEvent {
    /**
     * The name of the faction
     */
    String name;

    /**
     * @param instigator The CommandSource that instigated the event
     * @param name The name for the new faction
     */
    public FactionCreateEvent(@Nullable final CommandSource instigator, final String name) {
        super(instigator);
        this.name = name;
    }

    /**
     * Get the name for the new faction
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name for the new faction
     * @param name The name for the faction
     */
    public void setName(final String name) {
        this.name = name;
    }
}
