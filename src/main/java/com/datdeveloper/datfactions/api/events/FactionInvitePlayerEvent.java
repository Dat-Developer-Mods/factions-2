package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.Faction;
import com.datdeveloper.datfactions.factiondata.FactionPlayer;
import net.minecraft.commands.CommandSource;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fired when the faction name changes
 * <br>
 * Can be cancelled
 */
@Cancelable
public class FactionInvitePlayerEvent extends FactionEvent {
    /**
     * The player invited to the faction
     */
    final FactionPlayer invitedPlayer;

    /**
     * @param instigator The CommandSource that instigated the event
     * @param faction The faction the event is about
     * @param invitedPlayer The player invited to the faction
     */
    public FactionInvitePlayerEvent(@Nullable final CommandSource instigator, @NotNull final Faction faction, final FactionPlayer invitedPlayer) {
        super(instigator, faction);
        this.invitedPlayer = invitedPlayer;
    }

    /**
     * Get the player invited to the faction
     * @return the player invited to the faction
     */
    public FactionPlayer getInvitedPlayer() {
        return invitedPlayer;
    }
}
