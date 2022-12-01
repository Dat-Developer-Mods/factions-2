package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
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
public class FactionUninvitePlayerEvent extends FactionEvent {
    /**
     * The player uninvited from the faction
     */
    final FactionPlayer uninvitedPlayer;

    /**
     * @param instigator The CommandSource that instigated the event
     * @param faction The faction the event is about
     * @param uninvitedPlayer The player uninvited from the faction
     */
    public FactionUninvitePlayerEvent(@Nullable final CommandSource instigator, @NotNull final Faction faction, final FactionPlayer uninvitedPlayer) {
        super(instigator, faction);
        this.uninvitedPlayer = uninvitedPlayer;
    }

    /**
     * Get the player uninvited from the faction
     * @return the player uninvited from the faction
     */
    public FactionPlayer getUninvitedPlayer() {
        return uninvitedPlayer;
    }
}
