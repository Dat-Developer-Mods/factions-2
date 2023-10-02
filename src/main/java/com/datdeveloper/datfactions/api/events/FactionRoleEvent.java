package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.Faction;
import com.datdeveloper.datfactions.factiondata.permissions.FactionRole;
import org.jetbrains.annotations.NotNull;

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
     * @param faction The faction the event is about
     * @param role The role the event is about
     */
    protected FactionRoleEvent(@NotNull final Faction faction, @NotNull final FactionRole role) {
        super(faction);
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
