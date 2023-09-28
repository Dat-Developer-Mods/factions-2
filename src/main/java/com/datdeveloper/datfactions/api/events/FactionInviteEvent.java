package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.Faction;
import com.datdeveloper.datfactions.factiondata.FactionPlayer;
import net.minecraft.commands.CommandSource;
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
     * @param instigator The CommandSource that instigated the event
     * @param faction    The faction the event is about
     * @param player     The player invited to/uninvited from the faction
     * @param inviteType The type of invite (invited or uninvited)
     */
    protected FactionInviteEvent(@Nullable final CommandSource instigator, @NotNull final Faction faction, final @NotNull FactionPlayer player, final EInviteType inviteType) {
        super(instigator, faction);
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
    public class Pre extends FactionInviteEvent {
        /**
         * @param instigator    The CommandSource that instigated the event
         * @param faction       The faction the event is about
         * @param player        The player invited to/uninvited from the faction
         * @param inviteType    The type of invite (invited or uninvited)
         */
        public Pre(@Nullable final CommandSource instigator, @NotNull final Faction faction, final FactionPlayer player, final EInviteType inviteType) {
            super(instigator, faction, player, inviteType);
        }

        /**
         * Set the player that will be invited to/uninvited from the faction
         * @param newInvitedPlayer The new player that will be invited to/uninvited from the faction
         */
        public void setInvitedPlayer(final FactionPlayer newInvitedPlayer) {
            this.player = newInvitedPlayer;
        }
    }

    /**
     * Fired after a player has been invited to/uninvited from the faction
     * <br>
     * The intention of this event is to allow observing faction invites
     */
    public class Post extends FactionInviteEvent {
        /**
         * @param instigator    The CommandSource that instigated the event
         * @param faction       The faction the event is about
         * @param player        The player invited to/uninvited from the faction
         * @param inviteType    The type of invite (invited or uninvited)
         */
        public Post(@Nullable final CommandSource instigator, @NotNull final Faction faction, final FactionPlayer player, final EInviteType inviteType) {
            super(instigator, faction, player, inviteType);
        }
    }
}
