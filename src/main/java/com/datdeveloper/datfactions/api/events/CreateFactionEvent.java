package com.datdeveloper.datfactions.api.events;

import net.minecraft.commands.CommandSource;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.Nullable;

/**
 * Called when a faction is created
 * Can be cancelled, changes to the name will be reflected
 */
@Cancelable
public class CreateFactionEvent extends BaseFactionEvent {
    /**
     * The name of the faction
     */
    String name;
    public CreateFactionEvent(@Nullable final CommandSource instigator, final String name) {
        super(instigator);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
