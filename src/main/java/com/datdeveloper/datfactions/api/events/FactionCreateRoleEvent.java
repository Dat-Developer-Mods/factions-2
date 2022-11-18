package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factionData.Faction;
import net.minecraft.commands.CommandSource;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fired when a faction creates a role
 * Cancellable, and changes to newRoleName and newRoleParent will be reflected
 */
@Cancelable
public class FactionCreateRoleEvent extends FactionEvent {
    /**
     * The name of the new role
     */
    String newRoleName;

    /**
     * The role that the new role will be directly superior to
     */
    String newRoleParent;

    public FactionCreateRoleEvent(@Nullable CommandSource instigator, @NotNull Faction faction, String newRoleName, String newRoleParent) {
        super(instigator, faction);
        this.newRoleName = newRoleName;
        this.newRoleParent = newRoleParent;
    }

    public String getNewRoleName() {
        return newRoleName;
    }

    public void setNewRoleName(String newRoleName) {
        this.newRoleName = newRoleName;
    }

    public String getNewRoleParent() {
        return newRoleParent;
    }

    public void setNewRoleParent(String newRoleParent) {
        this.newRoleParent = newRoleParent;
    }
}
