package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.EFactionFlags;
import com.datdeveloper.datfactions.factiondata.Faction;
import com.datdeveloper.datfactions.factiondata.relations.EFactionRelation;
import com.datdeveloper.datfactions.factiondata.relations.FactionRelation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Events for when a faction changes its relation with another faction
 * @see FactionChangeRelationEvent.Pre
 * @see FactionChangeRelationEvent.Post
 */
public abstract class FactionChangeRelationEvent extends FactionEvent {
    /** The faction the relation is with */
    @NotNull
    Faction otherFaction;

    /** The new relation with the other faction */
    EFactionRelation newRelation;

    /**
     * @param faction The faction the event is about
     * @param otherFaction The faction the relation is with
     * @param newRelation The New Relation
     */
    protected FactionChangeRelationEvent(@NotNull final Faction faction, @NotNull final Faction otherFaction, final EFactionRelation newRelation) {
        super(faction);
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

    public abstract EFactionRelation getOldRelation();

    /**
     * Fired before the faction's relation with another faction changes
     * <br>
     * The purpose of this event is to allow modifying/checking a faction's change of relation before it is applied, for
     * example, this could be used to disallow certain faction relations
     * <p>
     *     This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.<br>
     *     If the event is cancelled, the change in relation will not occur
     * </p>
     */
    @Cancelable
    public static class Pre extends FactionChangeRelationEvent implements IFactionPreEvent {
        /** The instigator of the action (if there is one) */
        private final ServerPlayer instigator;

        /**
         * @param instigator   The player that instigated the event
         * @param faction      The faction the event is about
         * @param otherFaction The faction the relation is with
         * @param newRelation  the New Relation
         */
        public Pre(@Nullable final ServerPlayer instigator, @NotNull final Faction faction, @NotNull final Faction otherFaction, final EFactionRelation newRelation) {
            super(faction, otherFaction, newRelation);
            this.instigator = instigator;
        }

        /** {@inheritDoc} */
        @Override
        public EFactionRelation getOldRelation() {
            final FactionRelation relation = faction.getRelation(getOtherFaction());
            return relation == null ? null : relation.getRelation();
        }

        /**
         * Set the new relation
         * @param newRelation the newRelation
         */
        public void setNewRelation(final EFactionRelation newRelation) {
            this.newRelation = newRelation;
        }

        /**
         * Set the faction the relation will be with
         * @param otherFaction The new faction the relation will be with
         * @throws IllegalArgumentException When the other faction is the same as the source faction, or the other new
         * other faction is unrelatable
         */
        public void setOtherFaction(@NotNull final Faction otherFaction) {
            if (otherFaction.equals(faction)) throw new IllegalArgumentException("Other faction cannot be the same as the "
                    + "source faction");
            else if (otherFaction.hasFlag(EFactionFlags.UNRELATEABLE)) throw new IllegalArgumentException("That faction cannot form relations");

            this.otherFaction = otherFaction;
        }

        /** {@inheritDoc} */
        @Override
        public @Nullable ServerPlayer getInstigator() {
            return instigator;
        }
    }

    /**
     * Fired after the faction changes a relation with another faction
     * <br>
     * The intention of this event is to allow observing relation changes to update other resources
     */
    public static class Post extends FactionChangeRelationEvent {
        /** The previous relation with the faction */
        final EFactionRelation oldRelation;

        /**
         * @param faction      The faction the event is about
         * @param otherFaction The faction the relation is with
         * @param newRelation  The New Relation
         * @param oldRelation  The previous relation
         */
        public Post(@NotNull final Faction faction, @NotNull final Faction otherFaction, final EFactionRelation newRelation, final EFactionRelation oldRelation) {
            super(faction, otherFaction, newRelation);
            this.oldRelation = oldRelation;
        }

        /** {@inheritDoc} */
        @Override
        public EFactionRelation getOldRelation() {
            return oldRelation;
        }
    }
}
