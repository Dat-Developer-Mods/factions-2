package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.Faction;
import com.datdeveloper.datfactions.factiondata.permissions.FactionRole;
import net.minecraft.commands.CommandSource;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fired when a faction renames a role
 * <br>
 * Cancellable, changes to newName will be reflected
 */
@Cancelable
public class FactionRoleChangeNameEvent extends FactionRoleEvent {
    /**
     * The new name of the role
     */
    String newName;

    /**
     * @param instigator The CommandSource that instigated the event
     * @param faction The faction the event is about
     * @param role The role being removed
     */
    public FactionRoleChangeNameEvent(@Nullable final CommandSource instigator, @NotNull final Faction faction, @NotNull final FactionRole role, @NotNull final String newName) {
        super(instigator, faction, role);
        this.newName = newName;
    }

    /**
     * Get the new name of the role
     * @return the new name of the role
     */
    public String getNewName() {
        return newName;
    }

    /**
     * Set the new name of the role
     * @param newName The new name of the role
     */
    public void setNewName(final String newName) {
        this.newName = newName;
    }
}
