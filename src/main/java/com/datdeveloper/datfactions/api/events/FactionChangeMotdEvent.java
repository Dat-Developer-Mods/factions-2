package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.Faction;
import net.minecraft.commands.CommandSource;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fired when the faction name changes
 * <br>
 * Can be cancelled, changes to the motd will be reflected
 */
@Cancelable
public class FactionChangeMotdEvent extends FactionEvent {
    /**
     * The new MOTD of the faction
     */
    String newMotd;

    /**
     * @param instigator The CommandSource that instigated the event
     * @param faction The faction the event is about
     * @param newMotd The new MOTD of the faction
     */
    public FactionChangeMotdEvent(@Nullable final CommandSource instigator, @NotNull final Faction faction, final String newMotd) {
        super(instigator, faction);
        this.newMotd = newMotd;
    }

    /**
     * Get the new faction MOTD
     * @return the new MOTD
     */
    public String getNewMotd() {
        return newMotd;
    }

    /**
     * Set the new faction MOTD
     * @param newMotd the new MOTD
     */
    public void setNewMotd(final String newMotd) {
        this.newMotd = newMotd;
    }
}
