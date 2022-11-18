package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.permissions.FactionRole;
import net.minecraft.commands.CommandSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Parent to Faction events that operate on a role
 */
public abstract class FactionRoleEvent extends FactionEvent {
    /**
     * The role the event is for
     */
    @NotNull FactionRole role;

    public FactionRoleEvent(@Nullable CommandSource instigator, @NotNull Faction faction, @NotNull FactionRole role) {
        super(instigator, faction);
        this.role = role;
    }

    public @NotNull FactionRole getRole() {
        return role;
    }
}
