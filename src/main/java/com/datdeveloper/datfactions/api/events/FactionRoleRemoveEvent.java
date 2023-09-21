package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.Faction;
import com.datdeveloper.datfactions.factiondata.permissions.FactionRole;
import net.minecraft.commands.CommandSource;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fired when a faction removes a role
 * <br>
 * Cancellable
 */
@Cancelable
public class FactionRoleRemoveEvent extends FactionRoleEvent {
    /**
     * @param instigator The CommandSource that instigated the event
     * @param faction The faction the event is about
     * @param role The role being removed
     */
    public FactionRoleRemoveEvent(@Nullable final CommandSource instigator, @NotNull final Faction faction, @NotNull final FactionRole role) {
        super(instigator, faction, role);
    }
}
