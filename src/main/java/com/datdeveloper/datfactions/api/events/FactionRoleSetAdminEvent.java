package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.permissions.FactionRole;
import net.minecraft.commands.CommandSource;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fired when a faction changes the admin state of a role
 * <br>
 * Cancellable
 */
@Cancelable
public class FactionRoleSetAdminEvent extends FactionRoleEvent {
    /**
     * The new admin state
     */
    final boolean newAdmin;

    /**
     * @param instigator The CommandSource that instigated the event
     * @param faction The faction the event is about
     * @param role The role changing admin state
     * @param newAdmin The new admin state of the role
     */
    public FactionRoleSetAdminEvent(@Nullable final CommandSource instigator, @NotNull final Faction faction, @NotNull final FactionRole role, final boolean newAdmin) {
        super(instigator, faction, role);
        this.newAdmin = newAdmin;
    }

    /**
     * Get the new admin state of the role
     * @return the new admin state of the role
     */
    public boolean isNewAdmin() {
        return newAdmin;
    }
}
