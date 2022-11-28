package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.permissions.FactionRole;
import net.minecraft.commands.CommandSource;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fired when a faction changes the position of a role in the hierarchy
 * <br>
 * Cancellable, and changes to newParent will be reflected
 */
@Cancelable
public class FactionRoleChangeOrderEvent extends FactionRoleEvent {
    /**
     * The new parent of the role
     */
    @NotNull
    FactionRole newParent;

    public FactionRoleChangeOrderEvent(@Nullable final CommandSource instigator, @NotNull final Faction faction, @NotNull final FactionRole role, @NotNull final FactionRole newParent) {
        super(instigator, faction, role);
        this.newParent = newParent;
    }

    public @NotNull FactionRole getNewParent() {
        return newParent;
    }

    public void setNewParent(@NotNull final FactionRole newParent) {
        this.newParent = newParent;
    }
}
