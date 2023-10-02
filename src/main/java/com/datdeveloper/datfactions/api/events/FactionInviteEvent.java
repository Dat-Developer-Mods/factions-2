package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.Faction;
import com.datdeveloper.datfactions.factiondata.FactionPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Events for when a player is invited to/uninvited from a faction
 * @see FactionInviteEvent.Pre
 * @see FactionInviteEvent.Post
 */
public class FactionInviteEvent extends FactionEvent {
    /** The player invited to/uninvited from the faction */
    @NotNull FactionPlayer player;

    /** The type of invite (Invited or uninvited) */
    final EInviteType inviteType;

    /**
     * @param faction    The faction the event is about
     * @param player     The player invited to/uninvited from the faction
     * @param inviteType The type of invite (invited or uninvited)
     */
    protected FactionInviteEvent(@NotNull final Faction faction, final @NotNull FactionPlayer player, final EInviteType inviteType) {
        super(faction);
        this.player = player;
        this.inviteType = inviteType;
    }

    /**
     * Get the player invited to/uninvited from the faction
     * @return the player invited to the faction
     */
    public FactionPlayer getPlayer() {
        return player;
    }

    /**
     * An enum representing the types of invite event
     * <br>
     * Invite or uninvite
     */
    public enum EInviteType {
        /** Player was invited to the faction */
        INVITE,
        /** Player was uninvited from the faction */
        UNINVITE
    }

    /**
     * Fired before a player is invited to the faction
     * <br>
     * The purpose of this event is to allow preventing/redirecting of invites, for example to prevent invites to a
     * certain rank of players.
     * <p>
     *     After this event, the player will be checked to ensure they're not already in the faction and don't already
     *     have an invite
     * </p>
     * <p>
     *     This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.<br>
     *     If the event is cancelled, the player will not be invited
     * </p>
     */
    @Cancelable
    public static class Pre extends FactionInviteEvent implements IFactionPreEvent {
        /** The instigator of the action (if there is one) */
        private final ServerPlayer instigator;

        /**
         * @param instigator    The player that instigated the event
         * @param faction       The faction the event is about
         * @param player        The player invited to/uninvited from the faction
         * @param inviteType    The type of invite (invited or uninvited)
         */
        public Pre(@Nullable final ServerPlayer instigator, @NotNull final Faction faction, final FactionPlayer player, final EInviteType inviteType) {
            super(faction, player, inviteType);
            this.instigator = instigator;
        }

        /**
         * Set the player that will be invited to/uninvited from the faction
         * @param newInvitedPlayer The new player that will be invited to/uninvited from the faction
         */
        public void setInvitedPlayer(final FactionPlayer newInvitedPlayer) {
            this.player = newInvitedPlayer;
        }

        /** {@inheritDoc} */
        @Override
        public @Nullable ServerPlayer getInstigator() {
            return instigator;
        }
    }

    /**
     * Fired after a player has been invited to/uninvited from the faction
     * <br>
     * The intention of this event is to allow observing faction invites
     */
    public static class Post extends FactionInviteEvent {
        /**
         * @param faction       The faction the event is about
         * @param player        The player invited to/uninvited from the faction
         * @param inviteType    The type of invite (invited or uninvited)
         */
        public Post(@NotNull final Faction faction, final FactionPlayer player, final EInviteType inviteType) {
            super(faction, player, inviteType);
        }
    }
}
