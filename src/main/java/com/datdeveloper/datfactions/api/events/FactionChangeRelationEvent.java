package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factionData.EFactionRelation;
import com.datdeveloper.datfactions.factionData.Faction;
import net.minecraft.commands.CommandSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fired when a faction changes its relation with another faction
 * Cancellable, and changes to newRelation will be reflected.
 */
public class FactionChangeRelationEvent extends FactionEvent {
    /**
     * The faction the relation is with
     */
    @NotNull
    final Faction otherFaction;

    /**
     * The new relation with the other faction
     */
    EFactionRelation newRelation;

    public FactionChangeRelationEvent(@Nullable final CommandSource instigator, @NotNull final Faction faction, @NotNull final Faction otherFaction, final EFactionRelation newRelation) {
        super(instigator, faction);
        this.otherFaction = otherFaction;
        this.newRelation = newRelation;
    }

    public @NotNull Faction getOtherFaction() {
        return otherFaction;
    }

    public EFactionRelation getNewRelation() {
        return newRelation;
    }

    public void setNewRelation(final EFactionRelation newRelation) {
        this.newRelation = newRelation;
    }
}
