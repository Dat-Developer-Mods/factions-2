package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factionData.EFactionFlags;
import com.datdeveloper.datfactions.factionData.Faction;
import net.minecraft.commands.CommandSource;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Fired when a faction changes its flags
 * Cancellable, and changes to newFlags will be reflected
 */
@Cancelable
public class FactionChangeFlagsEvent extends FactionEvent {
    Set<EFactionFlags> newFlags;

    public FactionChangeFlagsEvent(@Nullable CommandSource instigator, @NotNull Faction faction, Set<EFactionFlags> newFlags) {
        super(instigator, faction);
        this.newFlags = newFlags;
    }

    public Set<EFactionFlags> getNewFlags() {
        return newFlags;
    }

    public void setNewFlags(Set<EFactionFlags> newFlags) {
        this.newFlags = newFlags;
    }
}
