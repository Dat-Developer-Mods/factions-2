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
    @NotNull
    final FactionRole role;

    /**
     * @param instigator The CommandSource that instigated the event
     * @param faction The faction the event is about
     * @param role The role the event is about
     */
    public FactionRoleEvent(@Nullable final CommandSource instigator, @NotNull final Faction faction, @NotNull final FactionRole role) {
        super(instigator, faction);
        this.role = role;
    }

    /**
     * Get the role the event is about
     * @return the role the event is about
     */
    public @NotNull FactionRole getRole() {
        return role;
    }
}
