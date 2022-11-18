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
    Faction otherFaction;

    /**
     * The new relation with the other faction
     */
    EFactionRelation newRelation;

    public FactionChangeRelationEvent(@Nullable CommandSource instigator, @NotNull Faction faction, @NotNull Faction otherFaction, EFactionRelation newRelation) {
        super(instigator, faction);
        this.otherFaction = otherFaction;
        this.newRelation = newRelation;
    }

    public Faction getOtherFaction() {
        return otherFaction;
    }

    public EFactionRelation getNewRelation() {
        return newRelation;
    }

    public void setNewRelation(EFactionRelation newRelation) {
        this.newRelation = newRelation;
    }
}
