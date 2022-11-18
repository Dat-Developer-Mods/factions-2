package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.permissions.FactionRole;
import net.minecraft.commands.CommandSource;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fired when a faction removes a role
 * Cancellable
 */
@Cancelable
public class FactionRemoveRoleEvent extends FactionRoleEvent {
    public FactionRemoveRoleEvent(@Nullable CommandSource instigator, @NotNull Faction faction, @NotNull FactionRole role) {
        super(instigator, faction, role);
    }
}
