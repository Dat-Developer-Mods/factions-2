package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factionData.relations.EFactionRelation;
import com.datdeveloper.datfactions.factionData.Faction;
import net.minecraft.commands.CommandSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fired when a faction changes its relation with another faction
 * <br>
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

    /**
     * @param instigator The CommandSource that instigated the event
     * @param faction The faction the event is about
     * @param otherFaction The faction the relation is with
     * @param newRelation the New Relation
     */
    public FactionChangeRelationEvent(@Nullable final CommandSource instigator, @NotNull final Faction faction, @NotNull final Faction otherFaction, final EFactionRelation newRelation) {
        super(instigator, faction);
        this.otherFaction = otherFaction;
        this.newRelation = newRelation;
    }

    /**
     * Get the faction the relation is with
     * @return the otherFaction
     */
    public @NotNull Faction getOtherFaction() {
        return otherFaction;
    }

    /**
     * Get the relation type
     * @return the newRelation
     */
    public EFactionRelation getNewRelation() {
        return newRelation;
    }

    /**
     * Set the new relation
     * @param newRelation the newRelation
     */
    public void setNewRelation(final EFactionRelation newRelation) {
        this.newRelation = newRelation;
    }
}
